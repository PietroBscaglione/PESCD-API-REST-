package br.ufscar.dc.dsw.PESCD.services;

import br.ufscar.dc.dsw.PESCD.dtos.AlunoMatriculaFormRecord;
import br.ufscar.dc.dsw.PESCD.dtos.CsvImportacaoResultadoDto;
import br.ufscar.dc.dsw.PESCD.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.models.UsuarioModel;
import br.ufscar.dc.dsw.PESCD.repositories.AlunoOfertaRepository;
import br.ufscar.dc.dsw.PESCD.repositories.UsuarioRepository;
import br.ufscar.dc.dsw.PESCD.util.CsvAlunoParser;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AlunoOfertaService {

    private static final Logger logger = LoggerFactory.getLogger(AlunoOfertaService.class);

    private final AlunoOfertaRepository alunoOfertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final OfertaService ofertaService;
    private final UsuarioService usuarioService;
    private final LogStatusAlunoOfertaService logStatusAlunoOfertaService;

    public AlunoOfertaService(
            AlunoOfertaRepository alunoOfertaRepository,
            UsuarioRepository usuarioRepository,
            OfertaService ofertaService,
            UsuarioService usuarioService,
            LogStatusAlunoOfertaService logStatusAlunoOfertaService) {
        this.alunoOfertaRepository = alunoOfertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.ofertaService = ofertaService;
        this.usuarioService = usuarioService;
        this.logStatusAlunoOfertaService = logStatusAlunoOfertaService;
    }

    @Transactional(readOnly = true)
    public List<AlunoOfertaModel> listarPorOferta(UUID ofertaId) {
        buscarOfertaEditavelOuSomenteLeitura(ofertaId);
        return alunoOfertaRepository.findByOfertaId(ofertaId);
    }

    @Transactional(readOnly = true)
    public AlunoOfertaModel buscarMatricula(UUID ofertaId, UUID matriculaId) {
        var matricula = alunoOfertaRepository.findWithDetalhesById(matriculaId)
                .orElseThrow(RecursoNaoEncontradoException::new);
        if (!matricula.getOferta().getId().equals(ofertaId)) {
            throw new RecursoNaoEncontradoException();
        }
        return matricula;
    }

    @Transactional
    public AlunoOfertaModel associar(UUID ofertaId, AlunoMatriculaFormRecord form, UsuarioModel operador) {
        var oferta = ofertaService.buscarPorId(ofertaId);
        ofertaService.assertOfertaEditavel(oferta);

        UsuarioModel aluno = resolverAluno(form);

        if (alunoOfertaRepository.existsByAlunoIdAndOfertaId(aluno.getId(), ofertaId)) {
            throw new ValidacaoNegocioException("aluno.oferta.error.ja.matriculado");
        }

        return criarMatricula(aluno, oferta, operador);
    }

    @Transactional
    public AlunoOfertaModel atualizar(UUID ofertaId, UUID matriculaId, AlunoMatriculaFormRecord form) {
        var oferta = ofertaService.buscarPorId(ofertaId);
        ofertaService.assertOfertaEditavel(oferta);

        var matricula = buscarMatricula(ofertaId, matriculaId);
        var aluno = matricula.getAluno();

        if (form.ra() != null && !form.ra().isBlank()) {
            aluno.setRa(form.ra().trim());
        }
        if (form.nomeCompleto() != null && !form.nomeCompleto().isBlank()) {
            aluno.setNomeCompleto(form.nomeCompleto().trim());
        }
        if (form.email() != null && !form.email().isBlank()) {
            var email = form.email().trim().toLowerCase();
            usuarioRepository.findByEmail(email).ifPresent(existente -> {
                if (!existente.getId().equals(aluno.getId())) {
                    throw new ValidacaoNegocioException("aluno.oferta.error.email.duplicado");
                }
            });
            aluno.setEmail(email);
            aluno.setUsername(email);
        }
        usuarioRepository.save(aluno);
        return matricula;
    }

    @Transactional
    public void desassociar(UUID ofertaId, UUID matriculaId) {
        var oferta = ofertaService.buscarPorId(ofertaId);
        ofertaService.assertOfertaEditavel(oferta);

        var matricula = buscarMatricula(ofertaId, matriculaId);
        if (matricula.getPlanoTrabalho() != null
                || matricula.getDocumentacaoDocencia() != null
                || matricula.getRelatorioFinal() != null) {
            throw new ValidacaoNegocioException("aluno.oferta.error.exclusao.com.documentos");
        }
        alunoOfertaRepository.delete(matricula);
        logger.info("Matricula removida: oferta={}, matricula={}", ofertaId, matriculaId);
    }

    @Transactional
    public CsvImportacaoResultadoDto importarCsv(UUID ofertaId, MultipartFile file, UsuarioModel operador) {
        var oferta = ofertaService.buscarPorId(ofertaId);
        ofertaService.assertOfertaEditavel(oferta);

        if (file == null || file.isEmpty()) {
            throw new ValidacaoNegocioException("csv.error.arquivo.vazio");
        }

        List<CsvAlunoParser.LinhaAlunoCsv> linhas;
        try {
            linhas = CsvAlunoParser.parse(file.getInputStream());
        } catch (IOException ex) {
            logger.error("Falha ao ler CSV", ex);
            throw new ValidacaoNegocioException("csv.error.leitura");
        }

        int criados = 0;
        int associados = 0;
        int erros = 0;

        for (var linha : linhas) {
            try {
                var alunoOpt = usuarioRepository.findByEmail(linha.email().toLowerCase());
                UsuarioModel aluno;
                if (alunoOpt.isPresent()) {
                    aluno = alunoOpt.get();
                } else {
                    aluno = usuarioService.criarAlunoCsv(linha.ra(), linha.nomeCompleto(), linha.email());
                    criados++;
                }

                if (!alunoOfertaRepository.existsByAlunoIdAndOfertaId(aluno.getId(), ofertaId)) {
                    criarMatricula(aluno, oferta, operador);
                    if (alunoOpt.isPresent()) {
                        associados++;
                    }
                }
            } catch (Exception ex) {
                logger.error("Erro ao processar linha CSV: ra={}, email={}", linha.ra(), linha.email(), ex);
                erros++;
            }
        }

        return new CsvImportacaoResultadoDto(criados, associados, erros);
    }

    private AlunoOfertaModel criarMatricula(UsuarioModel aluno, br.ufscar.dc.dsw.PESCD.models.OfertaModel oferta, UsuarioModel operador) {
        var matricula = new AlunoOfertaModel();
        matricula.setAluno(aluno);
        matricula.setOferta(oferta);
        matricula.setStatus(StatusAlunoOferta.NAO_ENVIADO);
        matricula.setCriadoEm(LocalDateTime.now());
        var salva = alunoOfertaRepository.save(matricula);
        logStatusAlunoOfertaService.registrar(
                salva,
                StatusAlunoOferta.NAO_ENVIADO,
                StatusAlunoOferta.NAO_ENVIADO,
                operador,
                "Matricula criada pelo secretario.");
        return salva;
    }

    private UsuarioModel resolverAluno(AlunoMatriculaFormRecord form) {
        if (form.email() != null && !form.email().isBlank()) {
            return usuarioRepository.findByEmail(form.email().trim().toLowerCase())
                    .orElseGet(() -> usuarioService.criarAlunoCsv(
                            form.ra(),
                            form.nomeCompleto(),
                            form.email()));
        }
        if (form.ra() != null && !form.ra().isBlank()) {
            return usuarioRepository.findByRa(form.ra().trim())
                    .orElseThrow(() -> new ValidacaoNegocioException("aluno.oferta.error.aluno.nao.encontrado"));
        }
        throw new ValidacaoNegocioException("aluno.oferta.error.identificacao.obrigatoria");
    }

    private br.ufscar.dc.dsw.PESCD.models.OfertaModel buscarOfertaEditavelOuSomenteLeitura(UUID ofertaId) {
        return ofertaService.buscarPorId(ofertaId);
    }
}

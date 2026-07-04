package br.ufscar.dc.dsw.PESCD.services;

import br.ufscar.dc.dsw.PESCD.dtos.RelatorioFinalForm;
import br.ufscar.dc.dsw.PESCD.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.LogStatusAlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.RelatorioFinalModel;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import br.ufscar.dc.dsw.PESCD.repositories.AlunoOfertaRepository;
import br.ufscar.dc.dsw.PESCD.repositories.RelatorioFinalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class RelatorioFinalService {

    private static final long TAMANHO_MAXIMO_BYTES = 5L * 1024L * 1024L;
    private static final Path DIRETORIO_RELATORIOS = Paths.get("uploads", "relatorios-finais");

    private final RelatorioFinalRepository relatorioFinalRepository;
    private final AlunoOfertaRepository alunoOfertaRepository;
    private final LogStatusAlunoOfertaService logStatusAlunoOfertaService;

    public RelatorioFinalService(
            RelatorioFinalRepository relatorioFinalRepository,
            AlunoOfertaRepository alunoOfertaRepository,
            LogStatusAlunoOfertaService logStatusAlunoOfertaService) {
        this.relatorioFinalRepository = relatorioFinalRepository;
        this.alunoOfertaRepository = alunoOfertaRepository;
        this.logStatusAlunoOfertaService = logStatusAlunoOfertaService;
    }

    @Transactional(readOnly = true)
    public AlunoOfertaModel buscarMatriculaElegivel(String username, UUID ofertaId) {
        var matricula = alunoOfertaRepository.findByAlunoUsernameAndOfertaId(username, ofertaId)
                .orElseThrow(RecursoNaoEncontradoException::new);
        validarPreCondicoes(matricula);
        return matricula;
    }

    @Transactional(readOnly = true)
    public List<LogStatusAlunoOfertaModel> listarHistorico(UUID alunoOfertaId) {
        return logStatusAlunoOfertaService.listarPorMatricula(alunoOfertaId);
    }

    @Transactional
    public RelatorioFinalModel enviarRelatorio(
            String username,
            UUID ofertaId,
            RelatorioFinalForm form,
            MultipartFile arquivo) {

        var matricula = alunoOfertaRepository.findByAlunoUsernameAndOfertaId(username, ofertaId)
                .orElseThrow(RecursoNaoEncontradoException::new);
        validarPreCondicoes(matricula);
        validarArquivo(arquivo);

        var relatorio = new RelatorioFinalModel();
        relatorio.setAlunoOferta(matricula);
        relatorio.setFrequenciaInformada(form.getFrequenciaInformada());
        relatorio.setArquivoRelatorio(armazenarArquivo(matricula.getId(), arquivo));
        relatorio.setEnviadoEm(LocalDateTime.now());

        var statusAnterior = matricula.getStatus();
        matricula.setStatus(StatusAlunoOferta.RELATORIO_ENVIADO);
        var relatorioSalvo = relatorioFinalRepository.save(relatorio);
        alunoOfertaRepository.save(matricula);
        logStatusAlunoOfertaService.registrar(
                matricula,
                statusAnterior,
                StatusAlunoOferta.RELATORIO_ENVIADO,
                matricula.getAluno(),
                "Relatorio final enviado pelo aluno.");

        return relatorioSalvo;
    }

    private void validarPreCondicoes(AlunoOfertaModel matricula) {
        if (matricula.getOferta().getStatus() != StatusOferta.EM_ANDAMENTO) {
            throw new ValidacaoNegocioException("relatorio.error.oferta.nao.andamento");
        }
        if (matricula.getStatus() != StatusAlunoOferta.PLANO_APROVADO) {
            throw new ValidacaoNegocioException("relatorio.error.status.invalido");
        }
        if (matricula.getPlanoTrabalho() == null) {
            throw new ValidacaoNegocioException("relatorio.error.plano.inexistente");
        }
        if (relatorioFinalRepository.findOptionalByAlunoOfertaId(matricula.getId()).isPresent()) {
            throw new ValidacaoNegocioException("relatorio.error.ja.enviado");
        }
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ValidacaoNegocioException("relatorio.error.arquivo.obrigatorio");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO_BYTES) {
            throw new ValidacaoNegocioException("relatorio.error.arquivo.tamanho");
        }
        var contentType = arquivo.getContentType();
        var nomeOriginal = arquivo.getOriginalFilename();
        var extensaoPdf = nomeOriginal != null && nomeOriginal.toLowerCase(Locale.ROOT).endsWith(".pdf");
        if (!extensaoPdf || !"application/pdf".equalsIgnoreCase(contentType)) {
            throw new ValidacaoNegocioException("relatorio.error.arquivo.pdf");
        }
    }

    private String armazenarArquivo(UUID matriculaId, MultipartFile arquivo) {
        try {
            Files.createDirectories(DIRETORIO_RELATORIOS);
            var destino = DIRETORIO_RELATORIOS.resolve(matriculaId + "-" + UUID.randomUUID() + ".pdf");
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return destino.toString();
        } catch (IOException ex) {
            throw new ValidacaoNegocioException("relatorio.error.arquivo.salvar");
        }
    }
}

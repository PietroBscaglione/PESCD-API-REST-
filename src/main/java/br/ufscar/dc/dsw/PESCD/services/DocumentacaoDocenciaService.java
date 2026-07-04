package br.ufscar.dc.dsw.PESCD.services;

import br.ufscar.dc.dsw.PESCD.dtos.DocumentacaoDocenciaForm;
import br.ufscar.dc.dsw.PESCD.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.DocumentacaoDocenciaModel;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import br.ufscar.dc.dsw.PESCD.models.TipoCredito;
import br.ufscar.dc.dsw.PESCD.repositories.AlunoOfertaRepository;
import br.ufscar.dc.dsw.PESCD.repositories.DocumentacaoDocenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
public class DocumentacaoDocenciaService {

    private static final long TAMANHO_MAXIMO_BYTES = 5L * 1024L * 1024L;
    private static final Path DIRETORIO_DOCUMENTOS = Paths.get("uploads", "documentacao-docencia");

    private final DocumentacaoDocenciaRepository documentacaoDocenciaRepository;
    private final AlunoOfertaRepository alunoOfertaRepository;
    private final LogStatusAlunoOfertaService logStatusAlunoOfertaService;

    public DocumentacaoDocenciaService(
            DocumentacaoDocenciaRepository documentacaoDocenciaRepository,
            AlunoOfertaRepository alunoOfertaRepository,
            LogStatusAlunoOfertaService logStatusAlunoOfertaService) {
        this.documentacaoDocenciaRepository = documentacaoDocenciaRepository;
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
    public DocumentacaoDocenciaForm consultarDocumentacao(String username, UUID ofertaId) {
        var matricula = alunoOfertaRepository.findByAlunoUsernameAndOfertaId(username, ofertaId)
                .orElseThrow(RecursoNaoEncontradoException::new);
        var form = new DocumentacaoDocenciaForm();
        preencherDadosMatricula(form, matricula);

        var documentacaoOptional = documentacaoDocenciaRepository.findOptionalByAlunoOfertaId(matricula.getId());
        if (documentacaoOptional.isEmpty()) {
            return form;
        }

        var documentacao = documentacaoOptional.get();
        form.setNomeInstituicao(documentacao.getNomeInstituicao());
        form.setNomeDisciplina(documentacao.getNomeDisciplina());
        form.setCursoDisciplina(documentacao.getCursoDisciplina());
        form.setCargaHoraria(documentacao.getCargaHoraria());
        form.setEnviadoEm(documentacao.getEnviadoEm());
        return form;
    }

    @Transactional
    public DocumentacaoDocenciaModel enviarDocumentacao(
            String username,
            UUID ofertaId,
            DocumentacaoDocenciaForm form,
            MultipartFile arquivo) {

        var matricula = alunoOfertaRepository.findByAlunoUsernameAndOfertaId(username, ofertaId)
                .orElseThrow(RecursoNaoEncontradoException::new);
        validarPreCondicoes(matricula);
        validarArquivo(arquivo);

        var documentacao = new DocumentacaoDocenciaModel();
        documentacao.setAlunoOferta(matricula);
        documentacao.setNomeInstituicao(form.getNomeInstituicao().trim());
        documentacao.setNomeDisciplina(form.getNomeDisciplina().trim());
        documentacao.setCursoDisciplina(form.getCursoDisciplina().trim());
        documentacao.setCargaHoraria(form.getCargaHoraria());
        documentacao.setArquivoDocumentacao(armazenarArquivo(matricula.getId(), arquivo));
        documentacao.setEnviadoEm(LocalDateTime.now());

        var statusAnterior = matricula.getStatus();
        matricula.setStatus(StatusAlunoOferta.DOCUMENTACAO_ENVIADA);
        matricula.setTipoCredito(TipoCredito.DOCUMENTACAO);
        var documentacaoSalva = documentacaoDocenciaRepository.save(documentacao);
        alunoOfertaRepository.save(matricula);
        logStatusAlunoOfertaService.registrar(
                matricula,
                statusAnterior,
                StatusAlunoOferta.DOCUMENTACAO_ENVIADA,
                matricula.getAluno(),
                "Documentacao de docencia enviada pelo aluno.");

        return documentacaoSalva;
    }

    private void validarPreCondicoes(AlunoOfertaModel matricula) {
        if (matricula.getOferta().getStatus() != StatusOferta.EM_ANDAMENTO) {
            throw new ValidacaoNegocioException("documentacao.error.oferta.nao.andamento");
        }
        if (matricula.getStatus() != StatusAlunoOferta.NAO_ENVIADO) {
            throw new ValidacaoNegocioException("documentacao.error.status.invalido");
        }
        if (documentacaoDocenciaRepository.findOptionalByAlunoOfertaId(matricula.getId()).isPresent()) {
            throw new ValidacaoNegocioException("documentacao.error.ja.enviada");
        }
    }

    private void preencherDadosMatricula(DocumentacaoDocenciaForm form, AlunoOfertaModel matricula) {
        var oferta = matricula.getOferta();
        form.setStatus(matricula.getStatus());
        form.setOfertaId(oferta.getId());
        form.setOfertaNome(oferta.getNome());
        form.setOfertaSemestre(oferta.getSemestre());
        form.setOfertaDataInicio(oferta.getDataInicio());
        form.setOfertaDataFim(oferta.getDataFim());
        form.setOfertaStatus(oferta.getStatus());
        form.setOfertaProfessorResponsavel(oferta.getProfessorResponsavel().getNomeCompleto());
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ValidacaoNegocioException("documentacao.error.arquivo.obrigatorio");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO_BYTES) {
            throw new ValidacaoNegocioException("documentacao.error.arquivo.tamanho");
        }
        var contentType = arquivo.getContentType();
        var nomeOriginal = arquivo.getOriginalFilename();
        var extensaoPdf = nomeOriginal != null && nomeOriginal.toLowerCase(Locale.ROOT).endsWith(".pdf");
        if (!extensaoPdf || !"application/pdf".equalsIgnoreCase(contentType)) {
            throw new ValidacaoNegocioException("documentacao.error.arquivo.pdf");
        }
    }

    private String armazenarArquivo(UUID matriculaId, MultipartFile arquivo) {
        try {
            Files.createDirectories(DIRETORIO_DOCUMENTOS);
            var destino = DIRETORIO_DOCUMENTOS.resolve(matriculaId + "-" + UUID.randomUUID() + ".pdf");
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return destino.toString();
        } catch (IOException ex) {
            throw new ValidacaoNegocioException("documentacao.error.arquivo.salvar");
        }
    }
}

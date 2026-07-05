package br.ufscar.dc.dsw.PESCD.controllers.api;

import br.ufscar.dc.dsw.PESCD.dtos.AlunoMatriculaFormRecord;
import br.ufscar.dc.dsw.PESCD.dtos.CsvImportacaoResultadoDto;
import br.ufscar.dc.dsw.PESCD.dtos.OfertaFormRecord;
import br.ufscar.dc.dsw.PESCD.dtos.OfertaListagemDto;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.AnaliseDocumentacaoModel;
import br.ufscar.dc.dsw.PESCD.models.AprovacaoPlanoModel;
import br.ufscar.dc.dsw.PESCD.models.AprovacaoRelatorioSupervisorModel;
import br.ufscar.dc.dsw.PESCD.models.ConclusaoRelatorioResponsavelModel;
import br.ufscar.dc.dsw.PESCD.models.DocumentacaoDocenciaModel;
import br.ufscar.dc.dsw.PESCD.models.LogStatusAlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.Nota;
import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import br.ufscar.dc.dsw.PESCD.models.PlanoTrabalhoModel;
import br.ufscar.dc.dsw.PESCD.models.RelatorioFinalModel;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOfertaExibicao;
import br.ufscar.dc.dsw.PESCD.models.TipoCredito;
import br.ufscar.dc.dsw.PESCD.models.UsuarioModel;
import br.ufscar.dc.dsw.PESCD.services.AlunoOfertaService;
import br.ufscar.dc.dsw.PESCD.services.ConfiguracaoSistemaService;
import br.ufscar.dc.dsw.PESCD.services.LogStatusAlunoOfertaService;
import br.ufscar.dc.dsw.PESCD.services.OfertaService;
import br.ufscar.dc.dsw.PESCD.services.UsuarioService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/secretario")
public class SecretarioApiController {

    private final OfertaService ofertaService;
    private final AlunoOfertaService alunoOfertaService;
    private final UsuarioService usuarioService;
    private final LogStatusAlunoOfertaService logStatusAlunoOfertaService;
    private final ConfiguracaoSistemaService configuracaoSistemaService;

    public SecretarioApiController(
            OfertaService ofertaService,
            AlunoOfertaService alunoOfertaService,
            UsuarioService usuarioService,
            LogStatusAlunoOfertaService logStatusAlunoOfertaService,
            ConfiguracaoSistemaService configuracaoSistemaService) {
        this.ofertaService = ofertaService;
        this.alunoOfertaService = alunoOfertaService;
        this.usuarioService = usuarioService;
        this.logStatusAlunoOfertaService = logStatusAlunoOfertaService;
        this.configuracaoSistemaService = configuracaoSistemaService;
    }

    @GetMapping("/professores-responsaveis")
    public List<UsuarioBasicoResponse> listarProfessoresResponsaveis() {
        return usuarioService.listarProfessoresParaOferta().stream()
                .map(this::toUsuarioBasicoResponse)
                .toList();
    }

    @GetMapping("/ofertas")
    public List<OfertaListaResponse> listarOfertas() {
        return ofertaService.listarParaAcompanhamento().stream()
                .map(this::toOfertaListaResponse)
                .toList();
    }

    @PostMapping("/ofertas")
    public ResponseEntity<OfertaDetalhesResponse> criarOferta(@Valid @RequestBody OfertaRequest request) {
        var secretario = usuarioService.obterLogado();
        var oferta = ofertaService.salvar(request.toRecord(), secretario);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(toOfertaDetalhesResponse(ofertaService.buscarDetalhes(oferta.getId())));
    }

    @GetMapping("/ofertas/{ofertaId}")
    public OfertaDetalhesResponse detalharOferta(@PathVariable UUID ofertaId) {
        return toOfertaDetalhesResponse(ofertaService.buscarDetalhes(ofertaId));
    }

    @GetMapping("/ofertas/{ofertaId}/exportar-resultados")
    public ResponseEntity<byte[]> exportarResultados(@PathVariable UUID ofertaId) {
        var resultado = ofertaService.exportarResultadosCsv(ofertaId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resultado.nomeArquivo() + "\"")
                .body(resultado.conteudo());
    }

    @GetMapping("/ofertas/{ofertaId}/alunos")
    public List<AlunoResumoResponse> listarAlunos(@PathVariable UUID ofertaId) {
        return alunoOfertaService.listarPorOferta(ofertaId).stream()
                .sorted(Comparator.comparing(matricula -> matricula.getAluno().getNomeCompleto(), String.CASE_INSENSITIVE_ORDER))
                .map(this::toAlunoResumoResponse)
                .toList();
    }

    @PostMapping("/ofertas/{ofertaId}/alunos")
    public ResponseEntity<AlunoResumoResponse> associarAluno(
            @PathVariable UUID ofertaId,
            @RequestBody AlunoRequest request) {
        var operador = usuarioService.obterLogado();
        var matricula = alunoOfertaService.associar(ofertaId, request.toRecord(), operador);
        return ResponseEntity.status(HttpStatus.CREATED).body(toAlunoResumoResponse(matricula));
    }

    @PutMapping("/ofertas/{ofertaId}/alunos/{matriculaId}")
    public AlunoResumoResponse atualizarAluno(
            @PathVariable UUID ofertaId,
            @PathVariable UUID matriculaId,
            @RequestBody AlunoRequest request) {
        return toAlunoResumoResponse(alunoOfertaService.atualizar(ofertaId, matriculaId, request.toRecord()));
    }

    @DeleteMapping("/ofertas/{ofertaId}/alunos/{matriculaId}")
    public ResponseEntity<Void> desassociarAluno(
            @PathVariable UUID ofertaId,
            @PathVariable UUID matriculaId) {
        alunoOfertaService.desassociar(ofertaId, matriculaId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            path = "/ofertas/{ofertaId}/alunos/importar-csv",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CsvImportacaoResultadoDto importarCsv(
            @PathVariable UUID ofertaId,
            @RequestParam("arquivo") MultipartFile arquivo) {
        var operador = usuarioService.obterLogado();
        return alunoOfertaService.importarCsv(ofertaId, arquivo, operador);
    }

    @GetMapping("/ofertas/{ofertaId}/alunos/{matriculaId}")
    public AlunoDetalhesResponse detalharAluno(
            @PathVariable UUID ofertaId,
            @PathVariable UUID matriculaId) {
        var matricula = alunoOfertaService.buscarMatricula(ofertaId, matriculaId);
        var logs = logStatusAlunoOfertaService.listarPorMatricula(matriculaId);
        return toAlunoDetalhesResponse(matricula, logs);
    }

    @GetMapping("/ofertas/{ofertaId}/encerramento")
    public EncerramentoOfertaResponse prepararEncerramento(@PathVariable UUID ofertaId) {
        var oferta = ofertaService.buscarPorId(ofertaId);
        return new EncerramentoOfertaResponse(
                toOfertaListaResponse(ofertaService.toListagemDto(oferta)),
                ofertaService.podeEncerrar(oferta),
                configuracaoSistemaService.obterInstrucoesEncerramento());
    }

    @PostMapping("/ofertas/{ofertaId}/encerrar")
    public OfertaDetalhesResponse encerrarOferta(@PathVariable UUID ofertaId) {
        var secretario = usuarioService.obterLogado();
        ofertaService.encerrar(ofertaId, secretario);
        return toOfertaDetalhesResponse(ofertaService.buscarDetalhes(ofertaId));
    }

    private OfertaListaResponse toOfertaListaResponse(OfertaListagemDto oferta) {
        return new OfertaListaResponse(
                oferta.id(),
                oferta.nome(),
                oferta.semestre(),
                oferta.dataInicio(),
                oferta.dataFim(),
                oferta.statusPersistido(),
                oferta.statusExibicao(),
                oferta.professorNome(),
                oferta.podeEncerrar());
    }

    private OfertaDetalhesResponse toOfertaDetalhesResponse(OfertaModel oferta) {
        var alunos = oferta.getAlunos().stream()
                .sorted(Comparator.comparing(matricula -> matricula.getAluno().getNomeCompleto(), String.CASE_INSENSITIVE_ORDER))
                .map(this::toAlunoResumoResponse)
                .toList();
        return new OfertaDetalhesResponse(
                toOfertaListaResponse(ofertaService.toListagemDto(oferta)),
                oferta.getCriadoEm(),
                toUsuarioBasicoResponse(oferta.getCriadoPor()),
                oferta.getEncerradoResponsavelEm(),
                oferta.getEncerradoSecretarioEm(),
                toUsuarioBasicoResponse(oferta.getEncerradoSecretarioPor()),
                oferta.getLicoesAprendidas(),
                alunos);
    }

    private AlunoResumoResponse toAlunoResumoResponse(AlunoOfertaModel matricula) {
        return new AlunoResumoResponse(
                matricula.getId(),
                toUsuarioBasicoResponse(matricula.getAluno()),
                matricula.getStatus(),
                matricula.getTipoCredito(),
                matricula.getFrequenciaFinal(),
                matricula.getNotaFinal(),
                matricula.getCriadoEm());
    }

    private AlunoDetalhesResponse toAlunoDetalhesResponse(
            AlunoOfertaModel matricula,
            List<LogStatusAlunoOfertaModel> logs) {
        return new AlunoDetalhesResponse(
                toOfertaListaResponse(ofertaService.toListagemDto(matricula.getOferta())),
                toAlunoResumoResponse(matricula),
                toPlanoResponse(matricula.getPlanoTrabalho()),
                toDocumentacaoResponse(matricula.getDocumentacaoDocencia()),
                toRelatorioResponse(matricula.getRelatorioFinal()),
                logs.stream()
                        .sorted(Comparator.comparing(LogStatusAlunoOfertaModel::getAlteradoEm))
                        .map(this::toLogStatusResponse)
                        .toList());
    }

    private PlanoResponse toPlanoResponse(PlanoTrabalhoModel plano) {
        if (plano == null) {
            return null;
        }
        return new PlanoResponse(
                plano.getId(),
                plano.getCodigoDisciplina(),
                plano.getNomeDisciplina(),
                plano.getCursoDisciplina(),
                toUsuarioBasicoResponse(plano.getProfessorSupervisor()),
                plano.getArquivoPlano(),
                plano.getEnviadoEm(),
                toAprovacaoPlanoResponse(plano.getAprovacaoPlano()));
    }

    private AprovacaoPlanoResponse toAprovacaoPlanoResponse(AprovacaoPlanoModel aprovacao) {
        if (aprovacao == null) {
            return null;
        }
        return new AprovacaoPlanoResponse(
                aprovacao.getId(),
                toUsuarioBasicoResponse(aprovacao.getAprovadoPor()),
                aprovacao.getParecer(),
                aprovacao.getAprovadoEm());
    }

    private DocumentacaoResponse toDocumentacaoResponse(DocumentacaoDocenciaModel documentacao) {
        if (documentacao == null) {
            return null;
        }
        return new DocumentacaoResponse(
                documentacao.getId(),
                documentacao.getNomeInstituicao(),
                documentacao.getNomeDisciplina(),
                documentacao.getCursoDisciplina(),
                documentacao.getCargaHoraria(),
                documentacao.getArquivoDocumentacao(),
                documentacao.getEnviadoEm(),
                toAnaliseDocumentacaoResponse(documentacao.getAnaliseDocumentacao()));
    }

    private AnaliseDocumentacaoResponse toAnaliseDocumentacaoResponse(AnaliseDocumentacaoModel analise) {
        if (analise == null) {
            return null;
        }
        return new AnaliseDocumentacaoResponse(
                analise.getId(),
                toUsuarioBasicoResponse(analise.getAnalisadoPor()),
                analise.getParecer(),
                analise.getFrequencia(),
                analise.getNota(),
                analise.getAnalisadoEm());
    }

    private RelatorioResponse toRelatorioResponse(RelatorioFinalModel relatorio) {
        if (relatorio == null) {
            return null;
        }
        return new RelatorioResponse(
                relatorio.getId(),
                relatorio.getFrequenciaInformada(),
                relatorio.getArquivoRelatorio(),
                relatorio.getEnviadoEm(),
                toAprovacaoRelatorioSupervisorResponse(relatorio.getAprovacaoSupervisor()),
                toConclusaoRelatorioResponsavelResponse(relatorio.getConclusaoResponsavel()));
    }

    private AprovacaoRelatorioSupervisorResponse toAprovacaoRelatorioSupervisorResponse(
            AprovacaoRelatorioSupervisorModel aprovacao) {
        if (aprovacao == null) {
            return null;
        }
        return new AprovacaoRelatorioSupervisorResponse(
                aprovacao.getId(),
                toUsuarioBasicoResponse(aprovacao.getAprovadoPor()),
                aprovacao.getParecer(),
                aprovacao.getFrequencia(),
                aprovacao.getSugestaoNota(),
                aprovacao.getAprovadoEm());
    }

    private ConclusaoRelatorioResponsavelResponse toConclusaoRelatorioResponsavelResponse(
            ConclusaoRelatorioResponsavelModel conclusao) {
        if (conclusao == null) {
            return null;
        }
        return new ConclusaoRelatorioResponsavelResponse(
                conclusao.getId(),
                toUsuarioBasicoResponse(conclusao.getConcluidoPor()),
                conclusao.getParecer(),
                conclusao.getFrequencia(),
                conclusao.getNota(),
                conclusao.getConcluidoEm());
    }

    private LogStatusResponse toLogStatusResponse(LogStatusAlunoOfertaModel log) {
        return new LogStatusResponse(
                log.getId(),
                log.getStatusAnterior(),
                log.getStatusNovo(),
                toUsuarioBasicoResponse(log.getAlteradoPor()),
                log.getAlteradoEm(),
                log.getObservacao());
    }

    private UsuarioBasicoResponse toUsuarioBasicoResponse(UsuarioModel usuario) {
        if (usuario == null) {
            return null;
        }
        return new UsuarioBasicoResponse(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getUsername());
    }

    public record OfertaRequest(
            String nome,
            @NotBlank String semestre,
            @NotNull LocalDate dataInicio,
            @NotNull LocalDate dataFim,
            @NotNull UUID professorResponsavelId
    ) {
        OfertaFormRecord toRecord() {
            return new OfertaFormRecord(nome, semestre, dataInicio, dataFim, professorResponsavelId);
        }
    }

    public record AlunoRequest(
            String ra,
            String nomeCompleto,
            String email
    ) {
        AlunoMatriculaFormRecord toRecord() {
            return new AlunoMatriculaFormRecord(ra, nomeCompleto, email);
        }
    }

    public record UsuarioBasicoResponse(
            UUID id,
            String nomeCompleto,
            String email,
            String username
    ) {
    }

    public record OfertaListaResponse(
            UUID id,
            String nome,
            String semestre,
            LocalDate dataInicio,
            LocalDate dataFim,
            StatusOferta statusPersistido,
            StatusOfertaExibicao statusExibicao,
            String professorResponsavelNome,
            boolean podeEncerrar
    ) {
    }

    public record OfertaDetalhesResponse(
            OfertaListaResponse oferta,
            LocalDateTime criadoEm,
            UsuarioBasicoResponse criadoPor,
            LocalDateTime encerradoResponsavelEm,
            LocalDateTime encerradoSecretarioEm,
            UsuarioBasicoResponse encerradoSecretarioPor,
            String licoesAprendidas,
            List<AlunoResumoResponse> alunos
    ) {
    }

    public record AlunoResumoResponse(
            UUID matriculaId,
            UsuarioBasicoResponse aluno,
            StatusAlunoOferta status,
            TipoCredito tipoCredito,
            Integer frequenciaFinal,
            Nota notaFinal,
            LocalDateTime criadoEm
    ) {
    }

    public record AlunoDetalhesResponse(
            OfertaListaResponse oferta,
            AlunoResumoResponse matricula,
            PlanoResponse plano,
            DocumentacaoResponse documentacao,
            RelatorioResponse relatorio,
            List<LogStatusResponse> logs
    ) {
    }

    public record PlanoResponse(
            UUID id,
            String codigoDisciplina,
            String nomeDisciplina,
            String cursoDisciplina,
            UsuarioBasicoResponse professorSupervisor,
            String arquivoPlano,
            LocalDateTime enviadoEm,
            AprovacaoPlanoResponse aprovacao
    ) {
    }

    public record AprovacaoPlanoResponse(
            UUID id,
            UsuarioBasicoResponse aprovadoPor,
            String parecer,
            LocalDateTime aprovadoEm
    ) {
    }

    public record DocumentacaoResponse(
            UUID id,
            String nomeInstituicao,
            String nomeDisciplina,
            String cursoDisciplina,
            Integer cargaHoraria,
            String arquivoDocumentacao,
            LocalDateTime enviadoEm,
            AnaliseDocumentacaoResponse analise
    ) {
    }

    public record AnaliseDocumentacaoResponse(
            UUID id,
            UsuarioBasicoResponse analisadoPor,
            String parecer,
            Integer frequencia,
            Nota nota,
            LocalDateTime analisadoEm
    ) {
    }

    public record RelatorioResponse(
            UUID id,
            Integer frequenciaInformada,
            String arquivoRelatorio,
            LocalDateTime enviadoEm,
            AprovacaoRelatorioSupervisorResponse aprovacaoSupervisor,
            ConclusaoRelatorioResponsavelResponse conclusaoResponsavel
    ) {
    }

    public record AprovacaoRelatorioSupervisorResponse(
            UUID id,
            UsuarioBasicoResponse aprovadoPor,
            String parecer,
            Integer frequencia,
            Nota sugestaoNota,
            LocalDateTime aprovadoEm
    ) {
    }

    public record ConclusaoRelatorioResponsavelResponse(
            UUID id,
            UsuarioBasicoResponse concluidoPor,
            String parecer,
            Integer frequencia,
            Nota nota,
            LocalDateTime concluidoEm
    ) {
    }

    public record LogStatusResponse(
            UUID id,
            StatusAlunoOferta statusAnterior,
            StatusAlunoOferta statusNovo,
            UsuarioBasicoResponse alteradoPor,
            LocalDateTime alteradoEm,
            String observacao
    ) {
    }

    public record EncerramentoOfertaResponse(
            OfertaListaResponse oferta,
            boolean permitido,
            List<String> instrucoes
    ) {
    }
}

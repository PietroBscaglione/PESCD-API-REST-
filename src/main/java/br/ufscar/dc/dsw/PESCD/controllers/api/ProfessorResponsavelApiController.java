package br.ufscar.dc.dsw.PESCD.controllers.api;

import br.ufscar.dc.dsw.PESCD.dtos.OfertaListagemDto;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.LogStatusAlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.Nota;
import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOfertaExibicao;
import br.ufscar.dc.dsw.PESCD.models.TipoCredito;
import br.ufscar.dc.dsw.PESCD.models.UsuarioModel;
import br.ufscar.dc.dsw.PESCD.services.ProfessorResponsavelService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/responsavel")
public class ProfessorResponsavelApiController {

    private final ProfessorResponsavelService professorResponsavelService;

    public ProfessorResponsavelApiController(ProfessorResponsavelService professorResponsavelService) {
        this.professorResponsavelService = professorResponsavelService;
    }

    // PR.04 - Dashboard: listar ofertas do professor responsável logado
    @GetMapping("/ofertas")
    public List<OfertaResumoResponse> listarOfertas(
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        return professorResponsavelService.listarOfertasParaAcompanhamento(usuarioAutenticado.getUsername())
                .stream()
                .map(this::toOfertaResumoResponse)
                .toList();
    }

    // PR.04 - Detalhes de uma oferta específica
    @GetMapping("/ofertas/{ofertaId}")
    public OfertaDetalhesResponse detalharOferta(
            @PathVariable UUID ofertaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        var username = usuarioAutenticado.getUsername();
        var oferta = professorResponsavelService.buscarOfertaDoResponsavel(username, ofertaId);
        var alunos = professorResponsavelService.listarAlunosDaOferta(username, ofertaId);
        var podeEncerrar = professorResponsavelService.podeEncerrarOferta(username, ofertaId);
        return toOfertaDetalhesResponse(oferta, alunos, podeEncerrar);
    }

    // PR.04 - Detalhes de uma matrícula/aluno
    @GetMapping("/ofertas/{ofertaId}/matriculas/{matriculaId}")
    public MatriculaDetalhesResponse detalharMatricula(
            @PathVariable UUID ofertaId,
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        var username = usuarioAutenticado.getUsername();
        var matricula = professorResponsavelService.buscarDetalhesMatricula(username, matriculaId);
        var logs = professorResponsavelService.listarHistorico(matriculaId);
        return toMatriculaDetalhesResponse(matricula, logs);
    }

    // PR.01 - Preparar conclusão de relatório (GET) - retorna dados pré-carregados
    @GetMapping("/matriculas/{matriculaId}/concluir-relatorio")
    public ConclusaoRelatorioPreparacaoResponse prepararConclusaoRelatorio(
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        var matricula = professorResponsavelService.buscarParaConcluirRelatorio(
                usuarioAutenticado.getUsername(), matriculaId);
        var aprovacaoSupervisor = matricula.getRelatorioFinal().getAprovacaoSupervisor();
        return new ConclusaoRelatorioPreparacaoResponse(
                toMatriculaResumoResponse(matricula),
                aprovacaoSupervisor != null ? aprovacaoSupervisor.getFrequencia() : null,
                aprovacaoSupervisor != null ? aprovacaoSupervisor.getSugestaoNota() : null);
    }

    // PR.01 - Concluir relatório de estágio (POST)
    @PostMapping("/matriculas/{matriculaId}/concluir-relatorio")
    public ResponseEntity<MatriculaResumoResponse> concluirRelatorio(
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @Valid @RequestBody ConclusaoRequest request) {
        professorResponsavelService.concluirRelatorio(
                usuarioAutenticado.getUsername(),
                matriculaId,
                request.parecer(),
                request.frequenciaFinal(),
                request.notaFinal());
        var matricula = professorResponsavelService.buscarDetalhesMatricula(
                usuarioAutenticado.getUsername(), matriculaId);
        return ResponseEntity.ok(toMatriculaResumoResponse(matricula));
    }

    // PR.02 - Preparar análise de documentação (GET)
    @GetMapping("/matriculas/{matriculaId}/analisar-documentacao")
    public AnaliseDocumentacaoPreparacaoResponse prepararAnaliseDocumentacao(
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        var matricula = professorResponsavelService.buscarParaAnalisarDocumentacao(
                usuarioAutenticado.getUsername(), matriculaId);
        return new AnaliseDocumentacaoPreparacaoResponse(
                toMatriculaResumoResponse(matricula),
                matricula.getDocumentacaoDocencia());
    }

    // PR.02 - Analisar documentação de docência (POST)
    @PostMapping("/matriculas/{matriculaId}/analisar-documentacao")
    public ResponseEntity<MatriculaResumoResponse> analisarDocumentacao(
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @Valid @RequestBody ConclusaoRequest request) {
        professorResponsavelService.analisarDocumentacao(
                usuarioAutenticado.getUsername(),
                matriculaId,
                request.parecer(),
                request.frequenciaFinal(),
                request.notaFinal());
        var matricula = professorResponsavelService.buscarDetalhesMatricula(
                usuarioAutenticado.getUsername(), matriculaId);
        return ResponseEntity.ok(toMatriculaResumoResponse(matricula));
    }

    // PR.03 - Verificar se pode encerrar oferta
    @GetMapping("/ofertas/{ofertaId}/pode-encerrar")
    public PodeEncerrarResponse podeEncerrarOferta(
            @PathVariable UUID ofertaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        var username = usuarioAutenticado.getUsername();
        var pode = professorResponsavelService.podeEncerrarOferta(username, ofertaId);
        var estatisticas = professorResponsavelService.calcularEstatisticas(username, ofertaId);
        return new PodeEncerrarResponse(pode, toEstatisticasResponse(estatisticas));
    }

    // PR.03 - Encerrar oferta (POST)
    @PostMapping("/ofertas/{ofertaId}/encerrar")
    public ResponseEntity<OfertaResumoResponse> encerrarOferta(
            @PathVariable UUID ofertaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @Valid @RequestBody EncerrarOfertaRequest request) {
        professorResponsavelService.encerrarOferta(
                usuarioAutenticado.getUsername(),
                ofertaId,
                request.licoesAprendidas());
        var oferta = professorResponsavelService.buscarResumoOferta(
                usuarioAutenticado.getUsername(), ofertaId);
        return ResponseEntity.ok(toOfertaResumoResponse(oferta));
    }

    // PR.03 - Estatísticas da oferta
    @GetMapping("/ofertas/{ofertaId}/estatisticas")
    public EstatisticasResponse calcularEstatisticas(
            @PathVariable UUID ofertaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        var estatisticas = professorResponsavelService.calcularEstatisticas(
                usuarioAutenticado.getUsername(), ofertaId);
        return toEstatisticasResponse(estatisticas);
    }

    // ========== DTOs internos ==========

    private OfertaResumoResponse toOfertaResumoResponse(OfertaListagemDto oferta) {
        return new OfertaResumoResponse(
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

    private OfertaDetalhesResponse toOfertaDetalhesResponse(
            OfertaModel oferta,
            List<AlunoOfertaModel> alunos,
            boolean podeEncerrar) {
        return new OfertaDetalhesResponse(
                oferta.getId(),
                oferta.getNome(),
                oferta.getSemestre(),
                oferta.getDataInicio(),
                oferta.getDataFim(),
                oferta.getStatus(),
                oferta.getCriadoEm(),
                oferta.getEncerradoResponsavelEm(),
                oferta.getLicoesAprendidas(),
                podeEncerrar,
                alunos.stream()
                        .sorted(Comparator.comparing(a -> a.getAluno().getNomeCompleto(), String.CASE_INSENSITIVE_ORDER))
                        .map(this::toMatriculaResumoResponse)
                        .toList());
    }

    private MatriculaResumoResponse toMatriculaResumoResponse(AlunoOfertaModel matricula) {
        return new MatriculaResumoResponse(
                matricula.getId(),
                matricula.getAluno().getId(),
                matricula.getAluno().getNomeCompleto(),
                matricula.getAluno().getEmail(),
                matricula.getStatus(),
                matricula.getTipoCredito(),
                matricula.getFrequenciaFinal(),
                matricula.getNotaFinal(),
                matricula.getCriadoEm());
    }

    private MatriculaDetalhesResponse toMatriculaDetalhesResponse(
            AlunoOfertaModel matricula,
            List<LogStatusAlunoOfertaModel> logs) {
        return new MatriculaDetalhesResponse(
                toMatriculaResumoResponse(matricula),
                logs.stream()
                        .sorted(Comparator.comparing(LogStatusAlunoOfertaModel::getAlteradoEm))
                        .map(this::toLogResponse)
                        .toList());
    }

    private LogResponse toLogResponse(LogStatusAlunoOfertaModel log) {
        return new LogResponse(
                log.getId(),
                log.getStatusAnterior(),
                log.getStatusNovo(),
                log.getAlteradoPor() != null ? log.getAlteradoPor().getNomeCompleto() : null,
                log.getAlteradoEm(),
                log.getObservacao());
    }

    private EstatisticasResponse toEstatisticasResponse(ProfessorResponsavelService.EstatisticasOferta estatisticas) {
        return new EstatisticasResponse(
                estatisticas.totalAlunos(),
                estatisticas.frequencias().getAverage(),
                estatisticas.frequencias().getMin(),
                estatisticas.frequencias().getMax(),
                estatisticas.totalEstagio(),
                estatisticas.totalDocumentacao(),
                estatisticas.totalNotaA(),
                estatisticas.totalNotaB(),
                estatisticas.totalNotaC(),
                estatisticas.totalNotaD(),
                estatisticas.totalNotaE());
    }

    // ========== Records ==========

    public record OfertaResumoResponse(
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
            UUID id,
            String nome,
            String semestre,
            LocalDate dataInicio,
            LocalDate dataFim,
            StatusOferta status,
            LocalDateTime criadoEm,
            LocalDateTime encerradoResponsavelEm,
            String licoesAprendidas,
            boolean podeEncerrar,
            List<MatriculaResumoResponse> alunos
    ) {
    }

    public record MatriculaResumoResponse(
            UUID matriculaId,
            UUID alunoId,
            String alunoNome,
            String alunoEmail,
            StatusAlunoOferta status,
            TipoCredito tipoCredito,
            Integer frequenciaFinal,
            Nota notaFinal,
            LocalDateTime criadoEm
    ) {
    }

    public record MatriculaDetalhesResponse(
            MatriculaResumoResponse matricula,
            List<LogResponse> logs
    ) {
    }

    public record LogResponse(
            UUID id,
            StatusAlunoOferta statusAnterior,
            StatusAlunoOferta statusNovo,
            String alteradoPorNome,
            LocalDateTime alteradoEm,
            String observacao
    ) {
    }

    public record ConclusaoRelatorioPreparacaoResponse(
            MatriculaResumoResponse matricula,
            Integer frequenciaSugerida,
            Nota notaSugerida
    ) {
    }

    public record AnaliseDocumentacaoPreparacaoResponse(
            MatriculaResumoResponse matricula,
            br.ufscar.dc.dsw.PESCD.models.DocumentacaoDocenciaModel documentacao
    ) {
    }

    public record ConclusaoRequest(
            @NotBlank String parecer,
            @NotNull @Min(0) @Max(100) Integer frequenciaFinal,
            @NotNull Nota notaFinal
    ) {
    }

    public record EncerrarOfertaRequest(
            @NotBlank String licoesAprendidas
    ) {
    }

    public record PodeEncerrarResponse(
            boolean podeEncerrar,
            EstatisticasResponse estatisticas
    ) {
    }

    public record EstatisticasResponse(
            int totalAlunos,
            double mediaFrequencia,
            double frequenciaMinima,
            double frequenciaMaxima,
            long totalEstagio,
            long totalDocumentacao,
            long totalNotaA,
            long totalNotaB,
            long totalNotaC,
            long totalNotaD,
            long totalNotaE
    ) {
    }
}
package br.ufscar.dc.dsw.PESCD.controllers.api;

import br.ufscar.dc.dsw.PESCD.dtos.DocumentacaoDocenciaForm;
import br.ufscar.dc.dsw.PESCD.dtos.PlanoTrabalhoForm;
import br.ufscar.dc.dsw.PESCD.dtos.RelatorioFinalForm;
import br.ufscar.dc.dsw.PESCD.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.services.DocumentacaoDocenciaService;
import br.ufscar.dc.dsw.PESCD.services.PlanoTrabalhoService;
import br.ufscar.dc.dsw.PESCD.services.RelatorioFinalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final PlanoTrabalhoService planoTrabalhoService;
    private final DocumentacaoDocenciaService documentacaoDocenciaService;
    private final RelatorioFinalService relatorioFinalService;

    public DocumentoController(
            PlanoTrabalhoService planoTrabalhoService,
            DocumentacaoDocenciaService documentacaoDocenciaService,
            RelatorioFinalService relatorioFinalService) {
        this.planoTrabalhoService = planoTrabalhoService;
        this.documentacaoDocenciaService = documentacaoDocenciaService;
        this.relatorioFinalService = relatorioFinalService;
    }

        @PostMapping(value = "/ofertas/{ofertaId}/plano-trabalho", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> enviarPlanoTrabalho(
            @AuthenticationPrincipal UserDetails usuarioLogado,
            @PathVariable UUID ofertaId,
            @RequestParam String codigoDisciplina,
            @RequestParam String nomeDisciplina,
            @RequestParam String cursoDisciplina,
            @RequestParam UUID professorSupervisorId,
            @RequestPart("arquivo") MultipartFile arquivo) {

        if (!isPdf(arquivo)) {
            return ResponseEntity.badRequest().build();
        }

        var form = new PlanoTrabalhoForm();
        form.setCodigoDisciplina(codigoDisciplina);
        form.setNomeDisciplina(nomeDisciplina);
        form.setCursoDisciplina(cursoDisciplina);
        form.setProfessorSupervisorId(professorSupervisorId);

        planoTrabalhoService.enviarPlano(usuarioLogado.getUsername(), ofertaId, form, arquivo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/ofertas/{ofertaId}/plano-trabalho")
    public ResponseEntity<PlanoTrabalhoForm> consultarPlanoTrabalho(
            @AuthenticationPrincipal UserDetails usuarioLogado,
            @PathVariable UUID ofertaId) {

        return ResponseEntity.ok(planoTrabalhoService.consultarPlano(usuarioLogado.getUsername(), ofertaId));
    }

    @PostMapping(value = "/ofertas/{ofertaId}/comprovante-docencia", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> enviarComprovanteDocencia(
            @AuthenticationPrincipal UserDetails usuarioLogado,
            @PathVariable UUID ofertaId,
            @RequestParam String nomeInstituicao,
            @RequestParam String nomeDisciplina,
            @RequestParam String cursoDisciplina,
            @RequestParam Integer cargaHoraria,
            @RequestPart("arquivo") MultipartFile arquivo) {

        if (!isPdf(arquivo)) {
            return ResponseEntity.badRequest().build();
        }

        var form = new DocumentacaoDocenciaForm();
        form.setNomeInstituicao(nomeInstituicao);
        form.setNomeDisciplina(nomeDisciplina);
        form.setCursoDisciplina(cursoDisciplina);
        form.setCargaHoraria(cargaHoraria);

        documentacaoDocenciaService.enviarDocumentacao(usuarioLogado.getUsername(), ofertaId, form, arquivo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/ofertas/{ofertaId}/comprovante-docencia")
    public ResponseEntity<DocumentacaoDocenciaForm> consultarComprovanteDocencia(
            @AuthenticationPrincipal UserDetails usuarioLogado,
            @PathVariable UUID ofertaId) {

        return ResponseEntity.ok(documentacaoDocenciaService.consultarDocumentacao(usuarioLogado.getUsername(), ofertaId));
    }

    @PostMapping(value = "/ofertas/{ofertaId}/relatorio-final", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> enviarRelatorioFinal(
            @AuthenticationPrincipal UserDetails usuarioLogado,
            @PathVariable UUID ofertaId,
            @RequestParam Integer indicadorFrequencia,
            @RequestPart("arquivo") MultipartFile arquivo) {

        if (!isPdf(arquivo)) {
            return ResponseEntity.badRequest().build();
        }

        var form = new RelatorioFinalForm();
        form.setFrequenciaInformada(indicadorFrequencia);

        relatorioFinalService.enviarRelatorio(usuarioLogado.getUsername(), ofertaId, form, arquivo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/ofertas/{ofertaId}/relatorio-final")
    public ResponseEntity<RelatorioFinalForm> consultarRelatorioFinal(
            @AuthenticationPrincipal UserDetails usuarioLogado,
            @PathVariable UUID ofertaId) {

        return ResponseEntity.ok(relatorioFinalService.consultarRelatorio(usuarioLogado.getUsername(), ofertaId));
    }

    @ExceptionHandler(ValidacaoNegocioException.class)
    public ResponseEntity<Map<String, String>> handleValidacao(ValidacaoNegocioException ex) {
        return ResponseEntity.badRequest().body(Map.of("erro", ex.getMessageKey()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> handleNaoEncontrado(RecursoNaoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erro", ex.getMessage()));
    }

    private boolean isPdf(MultipartFile arquivo) {
        return arquivo != null && MediaType.APPLICATION_PDF_VALUE.equalsIgnoreCase(arquivo.getContentType());
    }
}

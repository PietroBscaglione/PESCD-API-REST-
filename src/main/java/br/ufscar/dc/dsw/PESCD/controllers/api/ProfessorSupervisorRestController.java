package br.ufscar.dc.dsw.PESCD.controllers.api;

import br.ufscar.dc.dsw.PESCD.dtos.AprovarPlanoSupervisorForm;
import br.ufscar.dc.dsw.PESCD.dtos.AprovarRelatorioSupervisorForm;
import br.ufscar.dc.dsw.PESCD.dtos.DadosAprovacaoSupervisorDto.AprovacaoPlanoDetalheDto;
import br.ufscar.dc.dsw.PESCD.dtos.DadosAprovacaoSupervisorDto.AprovacaoRelatorioDetalheDto;
import br.ufscar.dc.dsw.PESCD.dtos.OfertaSupervisionadaListagemDto;
import br.ufscar.dc.dsw.PESCD.services.ProfessorSupervisorService;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supervisor/supervisao")
public class ProfessorSupervisorRestController {

    private final ProfessorSupervisorService professorSupervisorService;

    public ProfessorSupervisorRestController(ProfessorSupervisorService professorSupervisorService) {
        this.professorSupervisorService = professorSupervisorService;
    }

    @GetMapping
    public ResponseEntity<List<OfertaSupervisionadaListagemDto>> listarSupervisoes(
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {

        List<OfertaSupervisionadaListagemDto> supervisoes = professorSupervisorService.listarSupervisoes(usuarioAutenticado.getUsername());
        return ResponseEntity.ok(supervisoes);
    }

    @GetMapping("/{id}/aprovar-plano")
    public ResponseEntity<AprovacaoPlanoDetalheDto> exibirAprovacaoPlano(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {

        return ResponseEntity.ok(professorSupervisorService.buscarDadosAprovacaoPlano(
                usuarioAutenticado.getUsername(),
                id));
    }

        @PostMapping("/{id}/aprovar-plano")
        public ResponseEntity<?> aprovarPlano(
                @PathVariable UUID id,
                @AuthenticationPrincipal UserDetails usuarioAutenticado,
                @Valid @RequestBody AprovarPlanoSupervisorForm form) {

            try {
                professorSupervisorService.aprovarPlano(usuarioAutenticado.getUsername(), id, form);
                return ResponseEntity.ok().build();
            } catch (br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException ex) {
                return ResponseEntity.badRequest().body(ex.getMessageKey());
            }
        }

    @GetMapping("/{id}/aprovar-relatorio")
    public ResponseEntity<AprovacaoRelatorioDetalheDto> exibirAprovacaoRelatorio(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {

        return ResponseEntity.ok(professorSupervisorService.buscarDadosAprovacaoRelatorio(
                usuarioAutenticado.getUsername(),
                id));
    }

    @PostMapping("/{id}/aprovar-relatorio")
    public ResponseEntity<?> aprovarRelatorio(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @Valid @RequestBody AprovarRelatorioSupervisorForm form) {

        try {
            professorSupervisorService.aprovarRelatorio(usuarioAutenticado.getUsername(), id, form);
            return ResponseEntity.ok().build();
        } catch (br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException ex) {
            return ResponseEntity.badRequest().body(ex.getMessageKey());
        }
    }
    @GetMapping("/{id}/plano")
    public ResponseEntity<Resource> baixarPlano(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {

        var matricula = professorSupervisorService.buscarMatriculaSupervisionada(usuarioAutenticado.getUsername(), id);
        return criarRespostaPdf(matricula.getPlanoTrabalho().getArquivoPlano(), "plano-trabalho.pdf");
    }

    @GetMapping("/{id}/relatorio")
    public ResponseEntity<Resource> baixarRelatorio(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {

        var matricula = professorSupervisorService.buscarMatriculaSupervisionada(usuarioAutenticado.getUsername(), id);
        return criarRespostaPdf(matricula.getRelatorioFinal().getArquivoRelatorio(), "relatorio-final.pdf");
    }

    private ResponseEntity<Resource> criarRespostaPdf(String arquivo, String nomeArquivo) {
        var caminho = Paths.get(arquivo).normalize();
        var recurso = new FileSystemResource(caminho);

        if (!recurso.exists() || !recurso.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomeArquivo + "\"")
                .body(recurso);
    }
}

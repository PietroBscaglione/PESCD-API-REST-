package br.ufscar.dc.dsw.PESCD.controllers;

import br.ufscar.dc.dsw.PESCD.dtos.AprovarPlanoSupervisorForm;
import br.ufscar.dc.dsw.PESCD.dtos.AprovarRelatorioSupervisorForm;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.Nota;
import br.ufscar.dc.dsw.PESCD.services.ProfessorSupervisorService;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/supervisor/supervisao")
public class ProfessorSupervisorController {

    private final ProfessorSupervisorService professorSupervisorService;

    public ProfessorSupervisorController(ProfessorSupervisorService professorSupervisorService) {
        this.professorSupervisorService = professorSupervisorService;
    }

    @GetMapping
    public String listarSupervisoes(
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model) {
        model.addAttribute(
                "supervisoes",
                professorSupervisorService.listarSupervisoes(usuarioAutenticado.getUsername()));
        return "supervisor/lista-supervisao";
    }

    @GetMapping("/{id}/aprovar-plano")
    public String exibirAprovacaoPlano(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            var matricula = professorSupervisorService.buscarParaAprovarPlano(usuarioAutenticado.getUsername(), id);
            if (!model.containsAttribute("aprovarPlanoSupervisorForm")) {
                model.addAttribute("aprovarPlanoSupervisorForm", new AprovarPlanoSupervisorForm());
            }
            carregarModeloPlano(model, matricula);
            return "supervisor/aprovar-plano";
        } catch (ValidacaoNegocioException ex) {
            redirectAttributes.addFlashAttribute("errorMessageKey", ex.getMessageKey());
            return "redirect:/supervisor/supervisao";
        }
    }

    @PostMapping("/{id}/aprovar-plano")
    public String aprovarPlano(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @Valid @ModelAttribute("aprovarPlanoSupervisorForm") AprovarPlanoSupervisorForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        var matricula = professorSupervisorService.buscarParaAprovarPlano(usuarioAutenticado.getUsername(), id);

        if (bindingResult.hasErrors()) {
            carregarModeloPlano(model, matricula);
            return "supervisor/aprovar-plano";
        }

        try {
            professorSupervisorService.aprovarPlano(usuarioAutenticado.getUsername(), id, form);
        } catch (ValidacaoNegocioException ex) {
            model.addAttribute("errorMessageKey", ex.getMessageKey());
            carregarModeloPlano(model, matricula);
            return "supervisor/aprovar-plano";
        }

        redirectAttributes.addFlashAttribute("successMessageKey", "supervisor.sucesso.plano.aprovado");
        return "redirect:/supervisor/supervisao";
    }

    @GetMapping("/{id}/aprovar-relatorio")
    public String exibirAprovacaoRelatorio(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            var matricula = professorSupervisorService.buscarParaAprovarRelatorio(usuarioAutenticado.getUsername(), id);
            if (!model.containsAttribute("aprovarRelatorioSupervisorForm")) {
                var form = new AprovarRelatorioSupervisorForm();
                form.setFrequencia(matricula.getRelatorioFinal().getFrequenciaInformada());
                model.addAttribute("aprovarRelatorioSupervisorForm", form);
            }
            carregarModeloRelatorio(model, matricula);
            return "supervisor/aprovar-relatorio";
        } catch (ValidacaoNegocioException ex) {
            redirectAttributes.addFlashAttribute("errorMessageKey", ex.getMessageKey());
            return "redirect:/supervisor/supervisao";
        }
    }

    @PostMapping("/{id}/aprovar-relatorio")
    public String aprovarRelatorio(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @Valid @ModelAttribute("aprovarRelatorioSupervisorForm") AprovarRelatorioSupervisorForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        var matricula = professorSupervisorService.buscarParaAprovarRelatorio(usuarioAutenticado.getUsername(), id);

        if (bindingResult.hasErrors()) {
            carregarModeloRelatorio(model, matricula);
            return "supervisor/aprovar-relatorio";
        }

        try {
            professorSupervisorService.aprovarRelatorio(usuarioAutenticado.getUsername(), id, form);
        } catch (ValidacaoNegocioException ex) {
            model.addAttribute("errorMessageKey", ex.getMessageKey());
            carregarModeloRelatorio(model, matricula);
            return "supervisor/aprovar-relatorio";
        }

        redirectAttributes.addFlashAttribute("successMessageKey", "supervisor.sucesso.relatorio.aprovado");
        return "redirect:/supervisor/supervisao";
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

    private void carregarModeloPlano(Model model, AlunoOfertaModel matricula) {
        model.addAttribute("matricula", matricula);
        model.addAttribute("oferta", matricula.getOferta());
        model.addAttribute("plano", matricula.getPlanoTrabalho());
    }

    private void carregarModeloRelatorio(Model model, AlunoOfertaModel matricula) {
        carregarModeloPlano(model, matricula);
        model.addAttribute("relatorio", matricula.getRelatorioFinal());
        model.addAttribute("historico", professorSupervisorService.listarHistorico(matricula.getId()));
        model.addAttribute("notas", Nota.values());
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

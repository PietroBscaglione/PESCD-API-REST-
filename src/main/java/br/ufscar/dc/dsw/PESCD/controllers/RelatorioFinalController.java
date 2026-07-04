package br.ufscar.dc.dsw.PESCD.controllers;

import br.ufscar.dc.dsw.PESCD.dtos.RelatorioFinalForm;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.services.RelatorioFinalService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/aluno/oferta")
public class RelatorioFinalController {

    private final RelatorioFinalService relatorioFinalService;

    public RelatorioFinalController(RelatorioFinalService relatorioFinalService) {
        this.relatorioFinalService = relatorioFinalService;
    }

    @GetMapping("/{id}/enviar-relatorio")
    public String exibirFormulario(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model,
            RedirectAttributes redirectAttributes) {

        AlunoOfertaModel matricula;
        try {
            matricula = relatorioFinalService.buscarMatriculaElegivel(usuarioAutenticado.getUsername(), id);
        } catch (ValidacaoNegocioException ex) {
            redirectAttributes.addFlashAttribute("errorMessageKey", ex.getMessageKey());
            return "redirect:/aluno/ofertas";
        }
        if (!model.containsAttribute("relatorioFinalForm")) {
            model.addAttribute("relatorioFinalForm", new RelatorioFinalForm());
        }
        carregarModeloFormulario(model, matricula);
        return "aluno/enviar-relatorio";
    }

    @PostMapping("/{id}/enviar-relatorio")
    public String enviarRelatorio(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @Valid @ModelAttribute("relatorioFinalForm") RelatorioFinalForm form,
            BindingResult bindingResult,
            @RequestParam("arquivoRelatorio") MultipartFile arquivoRelatorio,
            Model model,
            RedirectAttributes redirectAttributes) {

        var matricula = relatorioFinalService.buscarMatriculaElegivel(usuarioAutenticado.getUsername(), id);

        if (bindingResult.hasErrors()) {
            carregarModeloFormulario(model, matricula);
            return "aluno/enviar-relatorio";
        }

        try {
            relatorioFinalService.enviarRelatorio(usuarioAutenticado.getUsername(), id, form, arquivoRelatorio);
        } catch (ValidacaoNegocioException ex) {
            model.addAttribute("errorMessageKey", ex.getMessageKey());
            carregarModeloFormulario(model, matricula);
            return "aluno/enviar-relatorio";
        }

        redirectAttributes.addFlashAttribute("successMessageKey", "relatorio.sucesso.enviado");
        return "redirect:/aluno/ofertas";
    }

    private void carregarModeloFormulario(Model model, AlunoOfertaModel matricula) {
        model.addAttribute("matricula", matricula);
        model.addAttribute("oferta", matricula.getOferta());
        model.addAttribute("plano", matricula.getPlanoTrabalho());
        model.addAttribute("historico", relatorioFinalService.listarHistorico(matricula.getId()));
    }
}

package br.ufscar.dc.dsw.PESCD.controllers;

import br.ufscar.dc.dsw.PESCD.dtos.PlanoTrabalhoForm;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import br.ufscar.dc.dsw.PESCD.services.PlanoTrabalhoService;
import br.ufscar.dc.dsw.PESCD.services.UsuarioService;
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
public class PlanoTrabalhoController {

    private final PlanoTrabalhoService planoTrabalhoService;
    private final UsuarioService usuarioService;

    public PlanoTrabalhoController(PlanoTrabalhoService planoTrabalhoService, UsuarioService usuarioService) {
        this.planoTrabalhoService = planoTrabalhoService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/{id}/enviar-plano")
    public String exibirFormulario(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model,
            RedirectAttributes redirectAttributes) {

        AlunoOfertaModel matricula;
        try {
            matricula = planoTrabalhoService.buscarMatriculaElegivel(usuarioAutenticado.getUsername(), id);
        } catch (ValidacaoNegocioException ex) {
            redirectAttributes.addFlashAttribute("errorMessageKey", ex.getMessageKey());
            return "redirect:/aluno/ofertas";
        }
        if (!model.containsAttribute("planoTrabalhoForm")) {
            model.addAttribute("planoTrabalhoForm", new PlanoTrabalhoForm());
        }
        carregarModeloFormulario(model, matricula.getOferta());
        return "aluno/enviar-plano";
    }

    @PostMapping("/{id}/enviar-plano")
    public String enviarPlano(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @Valid @ModelAttribute("planoTrabalhoForm") PlanoTrabalhoForm form,
            BindingResult bindingResult,
            @RequestParam("arquivoPlano") MultipartFile arquivoPlano,
            Model model,
            RedirectAttributes redirectAttributes) {

        var matricula = planoTrabalhoService.buscarMatriculaElegivel(usuarioAutenticado.getUsername(), id);

        if (bindingResult.hasErrors()) {
            carregarModeloFormulario(model, matricula.getOferta());
            return "aluno/enviar-plano";
        }

        try {
            planoTrabalhoService.enviarPlano(usuarioAutenticado.getUsername(), id, form, arquivoPlano);
        } catch (ValidacaoNegocioException ex) {
            model.addAttribute("errorMessageKey", ex.getMessageKey());
            carregarModeloFormulario(model, matricula.getOferta());
            return "aluno/enviar-plano";
        }

        redirectAttributes.addFlashAttribute("successMessageKey", "plano.sucesso.enviado");
        return "redirect:/aluno/ofertas";
    }

    private void carregarModeloFormulario(Model model, OfertaModel oferta) {
        model.addAttribute("oferta", oferta);
        model.addAttribute("supervisores", usuarioService.listarSupervisores());
    }
}

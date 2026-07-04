package br.ufscar.dc.dsw.PESCD.controllers;

import br.ufscar.dc.dsw.PESCD.dtos.AdminUsuarioForm;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.services.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
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

import java.util.UUID;

@Controller
@RequestMapping("/admin/usuarios")
public class AdminUsuarioController {

    private final UsuarioService usuarioService;

    public AdminUsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarUsuariosAdmin());
        return "admin/usuarios-lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        if (!model.containsAttribute("adminUsuarioForm")) {
            model.addAttribute("adminUsuarioForm", new AdminUsuarioForm());
        }
        carregarModeloFormulario(model, false);
        return "admin/usuarios-form";
    }

    @PostMapping("/novo")
    public String criar(
            @Valid @ModelAttribute("adminUsuarioForm") AdminUsuarioForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            carregarModeloFormulario(model, false);
            return "admin/usuarios-form";
        }
        try {
            usuarioService.criarUsuarioAdmin(form);
        } catch (ValidacaoNegocioException ex) {
            model.addAttribute("errorMessageKey", ex.getMessageKey());
            carregarModeloFormulario(model, false);
            return "admin/usuarios-form";
        }
        redirectAttributes.addFlashAttribute("successMessageKey", "admin.usuario.sucesso.criado");
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable UUID id, Model model) {
        if (!model.containsAttribute("adminUsuarioForm")) {
            model.addAttribute("adminUsuarioForm", usuarioService.prepararForm(id));
        }
        model.addAttribute("usuarioId", id);
        carregarModeloFormulario(model, true);
        return "admin/usuarios-form";
    }

    @PostMapping("/{id}/editar")
    public String atualizar(
            @PathVariable UUID id,
            @Valid @ModelAttribute("adminUsuarioForm") AdminUsuarioForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("usuarioId", id);
            carregarModeloFormulario(model, true);
            return "admin/usuarios-form";
        }
        try {
            usuarioService.atualizarUsuarioAdmin(id, form);
        } catch (ValidacaoNegocioException ex) {
            model.addAttribute("usuarioId", id);
            model.addAttribute("errorMessageKey", ex.getMessageKey());
            carregarModeloFormulario(model, true);
            return "admin/usuarios-form";
        }
        redirectAttributes.addFlashAttribute("successMessageKey", "admin.usuario.sucesso.atualizado");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.excluirUsuarioAdmin(id, usuarioAutenticado.getUsername());
            redirectAttributes.addFlashAttribute("successMessageKey", "admin.usuario.sucesso.excluido");
        } catch (ValidacaoNegocioException ex) {
            redirectAttributes.addFlashAttribute("errorMessageKey", ex.getMessageKey());
        } catch (DataIntegrityViolationException ex) {
            redirectAttributes.addFlashAttribute("errorMessageKey", "admin.usuario.error.exclusao.com.vinculos");
        }
        return "redirect:/admin/usuarios";
    }

    private void carregarModeloFormulario(Model model, boolean edicao) {
        model.addAttribute("perfis", usuarioService.listarPerfisGerenciaveis());
        model.addAttribute("edicao", edicao);
    }
}

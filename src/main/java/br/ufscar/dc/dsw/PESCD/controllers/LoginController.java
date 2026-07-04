package br.ufscar.dc.dsw.PESCD.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(
            @RequestParam Optional<String> error,
            @RequestParam Optional<String> logout,
            Model model) {

        error.ifPresent(value -> model.addAttribute("loginErrorMessageKey", resolveLoginErrorMessage(value)));
        logout.ifPresent(value -> model.addAttribute("logoutMessageKey", "login.logout.success"));
        return "login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("dashboardTitleKey", "dashboard.admin.title");
        model.addAttribute("dashboardDescriptionKey", "dashboard.admin.description");
        model.addAttribute("dashboardActionUrl", "/admin/usuarios");
        model.addAttribute("dashboardActionKey", "dashboard.admin.acao.usuarios");
        return "dashboard";
    }

    @GetMapping("/secretario/dashboard")
    public String secretarioDashboard(Model model) {
        model.addAttribute("dashboardTitleKey", "dashboard.secretario.title");
        model.addAttribute("dashboardDescriptionKey", "dashboard.secretario.description");
        model.addAttribute("dashboardActionUrl", "/secretario/ofertas");
        model.addAttribute("dashboardActionKey", "dashboard.secretario.acao.ofertas");
        return "dashboard";
    }

    @GetMapping("/aluno/dashboard")
    public String alunoDashboard(Model model) {
        model.addAttribute("dashboardTitleKey", "dashboard.aluno.title");
        model.addAttribute("dashboardDescriptionKey", "dashboard.aluno.description");
        model.addAttribute("dashboardActionUrl", "/aluno/ofertas");
        model.addAttribute("dashboardActionKey", "dashboard.aluno.acao.ofertas");
        return "dashboard";
    }

    @GetMapping("/supervisor/dashboard")
    public String supervisorDashboard(Model model) {
        model.addAttribute("dashboardTitleKey", "dashboard.supervisor.title");
        model.addAttribute("dashboardDescriptionKey", "dashboard.supervisor.description");
        model.addAttribute("dashboardActionUrl", "/supervisor/supervisao");
        model.addAttribute("dashboardActionKey", "dashboard.supervisor.acao.supervisao");
        return "dashboard";
    }

    @GetMapping("/responsavel/dashboard")
    public String responsavelDashboard(Model model) {
        return "redirect:/responsavel/ofertas";
    }

    @GetMapping("/acesso-negado")
    public String acessoNegado() {
        return "acesso-negado";
    }

    private String resolveLoginErrorMessage(String error) {
        return switch (error) {
            case "disabled" -> "login.error.disabled";
            case "role" -> "login.error.role";
            default -> "login.error.invalid";
        };
    }
}

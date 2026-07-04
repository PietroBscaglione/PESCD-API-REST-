package br.ufscar.dc.dsw.PESCD.controllers;

import br.ufscar.dc.dsw.PESCD.services.VisitanteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VisitanteController {

    private final VisitanteService visitanteService;

    public VisitanteController(VisitanteService visitanteService) {
        this.visitanteService = visitanteService;
    }

    @GetMapping({"/ofertas", "/ofertas/publicas"})
    public String listarOfertasPublicas(Model model) {
        model.addAttribute("ofertas", visitanteService.listarOfertasPublicas());
        return "visitante/ofertas";
    }
}

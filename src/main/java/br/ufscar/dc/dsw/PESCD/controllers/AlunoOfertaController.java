package br.ufscar.dc.dsw.PESCD.controllers;

import br.ufscar.dc.dsw.PESCD.services.OfertaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/aluno/ofertas")
public class AlunoOfertaController {

    private final OfertaService ofertaService;

    public AlunoOfertaController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @GetMapping
    public String listarOfertasDoAluno(
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model) {

        var ofertas = ofertaService.listarOfertasDoAlunoLogado(usuarioAutenticado.getUsername());
        model.addAttribute("ofertas", ofertas);
        return "aluno/lista_oferta";
    }
}

package br.ufscar.dc.dsw.PESCD.controllers;

import br.ufscar.dc.dsw.PESCD.dtos.OfertaDTO;
import br.ufscar.dc.dsw.PESCD.dtos.OfertaListagemDto;
import br.ufscar.dc.dsw.PESCD.services.OfertaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
public class AlunoController {

    private final OfertaService ofertaService;

    public AlunoController(OfertaService ofertaService) {
        this.ofertaService = ofertaService;
    }

    @GetMapping("/me/ofertas")
    public ResponseEntity<List<OfertaDTO>> listarOfertasDoAlunoLogado(
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {

        var ofertas = ofertaService.listarOfertasDoAlunoLogado(usuarioAutenticado.getUsername()).stream()
                .map(this::toOfertaDTO)
                .toList();

        return ResponseEntity.ok(ofertas);
    }

    private OfertaDTO toOfertaDTO(OfertaListagemDto oferta) {
        return new OfertaDTO(
                oferta.id(),
                oferta.nome(),
                oferta.semestre(),
                oferta.dataInicio(),
                oferta.dataFim(),
                oferta.professorNome(),
                oferta.statusExibicao()
        );
    }
}

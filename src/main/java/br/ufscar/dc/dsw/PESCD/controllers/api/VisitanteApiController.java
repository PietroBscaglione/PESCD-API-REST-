package br.ufscar.dc.dsw.PESCD.controllers.api;

import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOfertaExibicao;
import br.ufscar.dc.dsw.PESCD.services.VisitanteService;
import br.ufscar.dc.dsw.PESCD.util.StatusOfertaResolver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/ofertas")
public class VisitanteApiController {

    private final VisitanteService visitanteService;

    public VisitanteApiController(VisitanteService visitanteService) {
        this.visitanteService = visitanteService;
    }

    @GetMapping
    public List<OfertaPublicaResponse> listarOfertasPublicas() {
        var hoje = LocalDate.now();
        return visitanteService.listarOfertasPublicas().stream()
                .map(oferta -> toOfertaPublicaResponse(oferta, hoje))
                .toList();
    }

    private OfertaPublicaResponse toOfertaPublicaResponse(OfertaModel oferta, LocalDate hoje) {
        return new OfertaPublicaResponse(
                oferta.getId(),
                oferta.getNome(),
                oferta.getSemestre(),
                oferta.getDataInicio(),
                oferta.getDataFim(),
                oferta.getStatus(),
                StatusOfertaResolver.resolver(oferta, hoje),
                oferta.getProfessorResponsavel().getNomeCompleto());
    }

    public record OfertaPublicaResponse(
            java.util.UUID id,
            String nome,
            String semestre,
            LocalDate dataInicio,
            LocalDate dataFim,
            StatusOferta statusPersistido,
            StatusOfertaExibicao statusExibicao,
            String professorResponsavelNome
    ) {
    }
}
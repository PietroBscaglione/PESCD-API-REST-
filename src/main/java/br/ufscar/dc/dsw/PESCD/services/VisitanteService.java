package br.ufscar.dc.dsw.PESCD.services;

import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import br.ufscar.dc.dsw.PESCD.repositories.VisitanteOfertaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VisitanteService {

    private final VisitanteOfertaRepository visitanteOfertaRepository;

    public VisitanteService(VisitanteOfertaRepository visitanteOfertaRepository) {
        this.visitanteOfertaRepository = visitanteOfertaRepository;
    }

    @Transactional(readOnly = true)
    public List<OfertaModel> listarOfertasPublicas() {
        return visitanteOfertaRepository.findAllByOrderBySemestreDescDataInicioDescNomeAsc();
    }
}

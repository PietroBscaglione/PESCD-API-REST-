package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VisitanteOfertaRepository extends JpaRepository<OfertaModel, UUID> {

    @EntityGraph(attributePaths = {"professorResponsavel", "alunos"})
    List<OfertaModel> findAllByOrderBySemestreDescDataInicioDescNomeAsc();
}

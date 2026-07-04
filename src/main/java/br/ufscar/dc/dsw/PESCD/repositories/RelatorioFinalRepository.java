package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.RelatorioFinalModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface RelatorioFinalRepository extends JpaRepository<RelatorioFinalModel, UUID> {
    RelatorioFinalModel findByAlunoOfertaId(UUID alunoOfertaId);

    Optional<RelatorioFinalModel> findOptionalByAlunoOfertaId(UUID alunoOfertaId);
}

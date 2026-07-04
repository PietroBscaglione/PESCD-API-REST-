package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.ConclusaoRelatorioResponsavelModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ConclusaoRelatorioResponsavelRepository extends JpaRepository<ConclusaoRelatorioResponsavelModel, UUID> {
    ConclusaoRelatorioResponsavelModel findByRelatorioFinalId(UUID relatorioFinalId);

    boolean existsByConcluidoPorId(UUID concluidoPorId);
}

package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.AprovacaoRelatorioSupervisorModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AprovacaoRelatorioSupervisorRepository extends JpaRepository<AprovacaoRelatorioSupervisorModel, UUID> {
    AprovacaoRelatorioSupervisorModel findByRelatorioFinalId(UUID relatorioFinalId);

    boolean existsByAprovadoPorId(UUID aprovadoPorId);
}

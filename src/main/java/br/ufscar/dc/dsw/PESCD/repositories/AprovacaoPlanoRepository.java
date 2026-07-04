package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.AprovacaoPlanoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AprovacaoPlanoRepository extends JpaRepository<AprovacaoPlanoModel, UUID> {
    AprovacaoPlanoModel findByPlanoTrabalhoId(UUID planoTrabalhoId);

    boolean existsByAprovadoPorId(UUID aprovadoPorId);
}

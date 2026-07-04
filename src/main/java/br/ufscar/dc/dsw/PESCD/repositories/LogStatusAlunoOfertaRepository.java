package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.LogStatusAlunoOfertaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface LogStatusAlunoOfertaRepository extends JpaRepository<LogStatusAlunoOfertaModel, UUID> {
    List<LogStatusAlunoOfertaModel> findByAlunoOfertaIdOrderByAlteradoEmAsc(UUID alunoOfertaId);

    boolean existsByAlteradoPorId(UUID alteradoPorId);
}

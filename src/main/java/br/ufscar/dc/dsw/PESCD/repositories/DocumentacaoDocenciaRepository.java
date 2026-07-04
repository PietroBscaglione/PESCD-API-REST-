package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.DocumentacaoDocenciaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface DocumentacaoDocenciaRepository extends JpaRepository<DocumentacaoDocenciaModel, UUID> {
    DocumentacaoDocenciaModel findByAlunoOfertaId(UUID alunoOfertaId);

    Optional<DocumentacaoDocenciaModel> findOptionalByAlunoOfertaId(UUID alunoOfertaId);
}

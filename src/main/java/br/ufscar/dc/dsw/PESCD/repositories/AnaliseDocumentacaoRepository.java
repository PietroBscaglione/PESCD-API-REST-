package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.AnaliseDocumentacaoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface AnaliseDocumentacaoRepository extends JpaRepository<AnaliseDocumentacaoModel, UUID> {
    AnaliseDocumentacaoModel findByDocumentacaoDocenciaId(UUID documentacaoDocenciaId);

    boolean existsByAnalisadoPorId(UUID analisadoPorId);
}

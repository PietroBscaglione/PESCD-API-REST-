package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.PerfilModel;
import br.ufscar.dc.dsw.PESCD.models.PerfilUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerfilRepository extends JpaRepository<PerfilModel, Long> {
    Optional<PerfilModel> findByNome(PerfilUsuario nome);
}

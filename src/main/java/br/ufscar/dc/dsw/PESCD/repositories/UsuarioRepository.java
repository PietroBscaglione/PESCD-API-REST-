package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.PerfilUsuario;
import br.ufscar.dc.dsw.PESCD.models.UsuarioModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<UsuarioModel, UUID> {
    Optional<UsuarioModel> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Optional<UsuarioModel> findByRa(String ra);

    @EntityGraph(attributePaths = "perfis")
    Optional<UsuarioModel> findByUsername(String username);

    List<UsuarioModel> findByPerfisNome(PerfilUsuario perfil);

    @EntityGraph(attributePaths = "perfis")
    List<UsuarioModel> findDistinctByPerfisNomeIn(Collection<PerfilUsuario> perfis);

    default UsuarioModel findUsuarioModelByNomeUsuario(String nomeUsuario) {
        return findByUsername(nomeUsuario).orElse(null);
    }

    default List<UsuarioModel> findByPerfil(PerfilUsuario perfil) {
        return findByPerfisNome(perfil);
    }
}

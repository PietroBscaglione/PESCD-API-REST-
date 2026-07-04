package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OfertaRepository extends JpaRepository<OfertaModel, UUID> {
    @EntityGraph(attributePaths = {"professorResponsavel", "criadoPor"})
    List<OfertaModel> findByOrderBySemestreDesc();

    @EntityGraph(attributePaths = {"professorResponsavel", "criadoPor", "alunos", "alunos.aluno"})
    Optional<OfertaModel> findWithDetalhesById(UUID id);

    List<OfertaModel> findByStatus(StatusOferta status);

    List<OfertaModel> findByProfessorResponsavelIdOrderBySemestreDesc(UUID professorResponsavelId);

    boolean existsByProfessorResponsavelId(UUID professorResponsavelId);

    boolean existsByCriadoPorId(UUID criadoPorId);

    boolean existsByEncerradoSecretarioPorId(UUID encerradoSecretarioPorId);

    @EntityGraph(attributePaths = {"professorResponsavel", "criadoPor", "alunos"})
    List<OfertaModel> findByProfessorResponsavelUsernameOrderBySemestreDescDataInicioDescNomeAsc(String username);

    @EntityGraph(attributePaths = {"professorResponsavel"})
    @Query("""
            select oferta
            from OfertaModel oferta
            join oferta.alunos matricula
            where matricula.aluno.username = :username
            order by oferta.semestre desc, oferta.dataInicio desc
            """)
    List<OfertaModel> findOfertasDoAlunoByUsername(@Param("username") String username);
}

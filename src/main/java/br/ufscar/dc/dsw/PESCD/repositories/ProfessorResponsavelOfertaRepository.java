package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessorResponsavelOfertaRepository extends JpaRepository<OfertaModel, UUID> {

    @EntityGraph(attributePaths = {"professorResponsavel", "alunos"})
    List<OfertaModel> findByProfessorResponsavelUsernameOrderBySemestreDescDataInicioDescNomeAsc(String username);

    @EntityGraph(attributePaths = {
            "professorResponsavel",
            "alunos",
            "alunos.aluno",
            "alunos.planoTrabalho",
            "alunos.documentacaoDocencia",
            "alunos.relatorioFinal"
    })
    Optional<OfertaModel> findByIdAndProfessorResponsavelUsername(UUID id, String username);
}

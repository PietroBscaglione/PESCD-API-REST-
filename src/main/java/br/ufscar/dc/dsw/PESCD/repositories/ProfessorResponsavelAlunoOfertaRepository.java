package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProfessorResponsavelAlunoOfertaRepository extends JpaRepository<AlunoOfertaModel, UUID> {

    @EntityGraph(attributePaths = {
            "aluno",
            "oferta",
            "oferta.professorResponsavel",
            "planoTrabalho",
            "planoTrabalho.aprovacaoPlano",
            "planoTrabalho.professorSupervisor",
            "documentacaoDocencia",
            "documentacaoDocencia.analiseDocumentacao",
            "relatorioFinal",
            "relatorioFinal.aprovacaoSupervisor",
            "relatorioFinal.conclusaoResponsavel"
    })
    Optional<AlunoOfertaModel> findByIdAndOfertaProfessorResponsavelUsername(UUID id, String username);

    @EntityGraph(attributePaths = {"aluno", "oferta"})
    List<AlunoOfertaModel> findByOfertaIdAndOfertaProfessorResponsavelUsernameOrderByAlunoNomeCompletoAsc(
            UUID ofertaId,
            String username);
}

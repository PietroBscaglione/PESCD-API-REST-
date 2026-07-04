package br.ufscar.dc.dsw.PESCD.repositories;

import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlunoOfertaRepository extends JpaRepository<AlunoOfertaModel, UUID> {
    List<AlunoOfertaModel> findByAlunoId(UUID alunoId);

    boolean existsByAlunoId(UUID alunoId);

    @EntityGraph(attributePaths = {"aluno", "oferta"})
    List<AlunoOfertaModel> findByOfertaId(UUID ofertaId);

    @EntityGraph(attributePaths = {
            "aluno",
            "oferta",
            "oferta.professorResponsavel",
            "logsStatus",
            "logsStatus.alteradoPor",
            "planoTrabalho",
            "planoTrabalho.professorSupervisor",
            "planoTrabalho.aprovacaoPlano",
            "planoTrabalho.aprovacaoPlano.aprovadoPor",
            "documentacaoDocencia",
            "documentacaoDocencia.analiseDocumentacao",
            "documentacaoDocencia.analiseDocumentacao.analisadoPor",
            "relatorioFinal",
            "relatorioFinal.aprovacaoSupervisor",
            "relatorioFinal.aprovacaoSupervisor.aprovadoPor",
            "relatorioFinal.conclusaoResponsavel",
            "relatorioFinal.conclusaoResponsavel.concluidoPor"
    })
    Optional<AlunoOfertaModel> findWithDetalhesById(UUID id);

    @EntityGraph(attributePaths = {"aluno", "oferta", "oferta.professorResponsavel", "planoTrabalho"})
    Optional<AlunoOfertaModel> findByAlunoUsernameAndOfertaId(String username, UUID ofertaId);

    @Query("""
            select distinct ao
            from AlunoOfertaModel ao
            join fetch ao.aluno aluno
            join fetch ao.oferta oferta
            join fetch oferta.professorResponsavel
            join fetch ao.planoTrabalho plano
            join fetch plano.professorSupervisor
            left join fetch ao.relatorioFinal
            where plano.professorSupervisor.username = :username
            order by oferta.semestre desc, oferta.nome asc, aluno.nomeCompleto asc
            """)
    List<AlunoOfertaModel> findSupervisionadasBySupervisorUsername(@Param("username") String username);

    @Query("""
            select distinct ao
            from AlunoOfertaModel ao
            join fetch ao.aluno
            join fetch ao.oferta oferta
            join fetch oferta.professorResponsavel
            join fetch ao.planoTrabalho plano
            join fetch plano.professorSupervisor
            left join fetch ao.relatorioFinal
            where ao.id = :id and plano.professorSupervisor.username = :username
            """)
    Optional<AlunoOfertaModel> findSupervisionadaByIdAndSupervisorUsername(
            @Param("id") UUID id,
            @Param("username") String username);

    List<AlunoOfertaModel> findByStatus(StatusAlunoOferta status);

    boolean existsByAlunoIdAndOfertaId(UUID alunoId, UUID ofertaId);
}
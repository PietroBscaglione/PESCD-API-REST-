package br.ufscar.dc.dsw.PESCD.dtos;

import br.ufscar.dc.dsw.PESCD.models.Nota;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public final class DadosAprovacaoSupervisorDto {

    private DadosAprovacaoSupervisorDto() {
    }

    public record AprovacaoPlanoDetalheDto(
            MatriculaResumoDto matricula,
            AlunoResumoDto aluno,
            OfertaResumoDto oferta,
            PlanoDetalheDto plano
    ) {
    }

    public record AprovacaoRelatorioDetalheDto(
            MatriculaResumoDto matricula,
            AlunoResumoDto aluno,
            OfertaResumoDto oferta,
            PlanoDetalheDto plano,
            RelatorioDetalheDto relatorio,
            List<HistoricoStatusDto> historico,
            List<Nota> notas
    ) {
    }

    public record MatriculaResumoDto(
            UUID id,
            StatusAlunoOferta status
    ) {
    }

    public record AlunoResumoDto(
            String nomeCompleto,
            String ra
    ) {
    }

    public record OfertaResumoDto(
            UUID id,
            String nome,
            String semestre,
            LocalDate dataInicio,
            LocalDate dataFim,
            String professorResponsavelNome
    ) {
    }

    public record PlanoDetalheDto(
            UUID id,
            String codigoDisciplina,
            String nomeDisciplina,
            String cursoDisciplina,
            LocalDateTime enviadoEm,
            String urlPdf
    ) {
    }

    public record RelatorioDetalheDto(
            UUID id,
            Integer frequenciaInformada,
            LocalDateTime enviadoEm,
            String urlPdf
    ) {
    }

    public record HistoricoStatusDto(
            UUID id,
            StatusAlunoOferta statusAnterior,
            StatusAlunoOferta statusNovo,
            String alteradoPorNome,
            LocalDateTime alteradoEm,
            String observacao
    ) {
    }
}

package br.ufscar.dc.dsw.PESCD.dtos;

import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OfertaSupervisionadaListagemDto(
        OfertaSupervisionadaResumoDto oferta,
        List<MatriculaSupervisionadaListagemDto> alunos
) {
    public record OfertaSupervisionadaResumoDto(
            UUID id,
            String nome,
            String semestre,
            LocalDate dataInicio,
            LocalDate dataFim,
            String professorResponsavelNome
    ) {
    }

    public record MatriculaSupervisionadaListagemDto(
            UUID id,
            AlunoSupervisionadoResumoDto aluno,
            StatusAlunoOferta status
    ) {
    }

    public record AlunoSupervisionadoResumoDto(
            String nomeCompleto,
            String ra
    ) {
    }
}

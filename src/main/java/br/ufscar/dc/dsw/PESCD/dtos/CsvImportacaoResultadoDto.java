package br.ufscar.dc.dsw.PESCD.dtos;

public record CsvImportacaoResultadoDto(
        int alunosCriados,
        int alunosAssociados,
        int linhasComErro
) {
}

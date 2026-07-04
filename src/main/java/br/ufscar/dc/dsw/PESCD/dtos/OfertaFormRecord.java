package br.ufscar.dc.dsw.PESCD.dtos;

import java.time.LocalDate;
import java.util.UUID;

public record OfertaFormRecord(
        String nome,
        String semestre,
        LocalDate dataInicio,
        LocalDate dataFim,
        UUID professorResponsavelId
) {
}

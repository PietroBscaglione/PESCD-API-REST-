package br.ufscar.dc.dsw.PESCD.dtos;

import br.ufscar.dc.dsw.PESCD.models.StatusOfertaExibicao;

import java.time.LocalDate;
import java.util.UUID;

public record OfertaDTO(
        UUID id,
        String nomeOferta,
        String semestre,
        LocalDate dataInicio,
        LocalDate dataFim,
        String professorResponsavel,
        StatusOfertaExibicao statusOferta
) {
}

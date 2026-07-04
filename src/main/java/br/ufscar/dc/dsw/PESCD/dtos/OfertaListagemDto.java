package br.ufscar.dc.dsw.PESCD.dtos;

import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOfertaExibicao;

import java.time.LocalDate;
import java.util.UUID;

public record OfertaListagemDto(
        UUID id,
        String nome,
        String semestre,
        LocalDate dataInicio,
        LocalDate dataFim,
        StatusOferta statusPersistido,
        StatusOfertaExibicao statusExibicao,
        String professorNome,
        boolean podeEncerrar
) {
}

package br.ufscar.dc.dsw.PESCD.dtos;

import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.OfertaModel;

import java.util.List;

public record OfertaSupervisionadaDto(OfertaModel oferta, List<AlunoOfertaModel> alunos) {
}

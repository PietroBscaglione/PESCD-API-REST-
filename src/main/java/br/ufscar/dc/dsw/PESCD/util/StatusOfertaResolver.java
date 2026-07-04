package br.ufscar.dc.dsw.PESCD.util;

import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOfertaExibicao;

import java.time.LocalDate;

public final class StatusOfertaResolver {

    private StatusOfertaResolver() {
    }

    public static StatusOfertaExibicao resolver(OfertaModel oferta, LocalDate hoje) {
        if (oferta.getStatus() == StatusOferta.CONCLUIDA) {
            return StatusOfertaExibicao.CONCLUIDA;
        }
        if (oferta.getStatus() == StatusOferta.AGUARDANDO_ENCERRAMENTO_SECRETARIO) {
            return StatusOfertaExibicao.AGUARDANDO_ENCERRAMENTO_SECRETARIO;
        }
        if (hoje.isAfter(oferta.getDataFim())) {
            return StatusOfertaExibicao.EM_ATRASO;
        }
        if (hoje.isBefore(oferta.getDataInicio())) {
            return StatusOfertaExibicao.AGUARDANDO_INICIO;
        }
        if (hoje.isAfter(oferta.getDataInicio()) && hoje.isBefore(oferta.getDataFim())) {
            return StatusOfertaExibicao.EM_ANDAMENTO;
        }
        return StatusOfertaExibicao.EM_ANDAMENTO;
    }
}

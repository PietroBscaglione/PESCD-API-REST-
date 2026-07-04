package br.ufscar.dc.dsw.PESCD.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RelatorioFinalForm {
    private Integer frequenciaInformada;

    public Integer getFrequenciaInformada() {
        return frequenciaInformada;
    }

    public void setFrequenciaInformada(Integer frequenciaInformada) {
        this.frequenciaInformada = frequenciaInformada;
    }
}

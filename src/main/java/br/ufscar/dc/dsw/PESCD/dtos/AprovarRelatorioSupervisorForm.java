package br.ufscar.dc.dsw.PESCD.dtos;

import br.ufscar.dc.dsw.PESCD.models.Nota;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AprovarRelatorioSupervisorForm {
    @NotBlank(message = "{supervisor.error.parecer.obrigatorio}")
    private String parecer;

    @NotNull(message = "{supervisor.error.frequencia.obrigatoria}")
    @Min(value = 0, message = "{supervisor.error.frequencia.minima}")
    @Max(value = 100, message = "{supervisor.error.frequencia.maxima}")
    private Integer frequencia;

    @NotNull(message = "{supervisor.error.nota.obrigatoria}")
    private Nota sugestaoNota;

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public Integer getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(Integer frequencia) {
        this.frequencia = frequencia;
    }

    public Nota getSugestaoNota() {
        return sugestaoNota;
    }

    public void setSugestaoNota(Nota sugestaoNota) {
        this.sugestaoNota = sugestaoNota;
    }
}

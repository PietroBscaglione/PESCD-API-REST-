package br.ufscar.dc.dsw.PESCD.dtos;

import br.ufscar.dc.dsw.PESCD.models.Nota;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ConclusaoResponsavelForm {
    @NotBlank(message = "{responsavel.error.parecer.obrigatorio}")
    private String parecer;

    @NotNull(message = "{responsavel.error.frequencia.obrigatoria}")
    @Min(value = 0, message = "{responsavel.error.frequencia.minima}")
    @Max(value = 100, message = "{responsavel.error.frequencia.maxima}")
    private Integer frequenciaFinal;

    @NotNull(message = "{responsavel.error.nota.obrigatoria}")
    private Nota notaFinal;

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }

    public Integer getFrequenciaFinal() {
        return frequenciaFinal;
    }

    public void setFrequenciaFinal(Integer frequenciaFinal) {
        this.frequenciaFinal = frequenciaFinal;
    }

    public Nota getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(Nota notaFinal) {
        this.notaFinal = notaFinal;
    }
}

package br.ufscar.dc.dsw.PESCD.dtos;

import jakarta.validation.constraints.NotBlank;

public class AprovarPlanoSupervisorForm {
    @NotBlank(message = "{supervisor.error.parecer.obrigatorio}")
    private String parecer;

    public String getParecer() {
        return parecer;
    }

    public void setParecer(String parecer) {
        this.parecer = parecer;
    }
}

package br.ufscar.dc.dsw.PESCD.dtos;

import jakarta.validation.constraints.NotBlank;

public class EncerrarOfertaResponsavelForm {
    @NotBlank(message = "{responsavel.error.licoes.obrigatorio}")
    private String licoesAprendidas;

    public String getLicoesAprendidas() {
        return licoesAprendidas;
    }

    public void setLicoesAprendidas(String licoesAprendidas) {
        this.licoesAprendidas = licoesAprendidas;
    }
}

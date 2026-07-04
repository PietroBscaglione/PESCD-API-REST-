package br.ufscar.dc.dsw.PESCD.dtos;

import java.time.LocalDate;
import java.util.UUID;

public class OfertaForm {
    private String nome;
    private String semestre;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private UUID professorResponsavelId;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSemestre() {
        return semestre;
    }

    public void setSemestre(String semestre) {
        this.semestre = semestre;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public UUID getProfessorResponsavelId() {
        return professorResponsavelId;
    }

    public void setProfessorResponsavelId(UUID professorResponsavelId) {
        this.professorResponsavelId = professorResponsavelId;
    }

    public OfertaFormRecord toRecord() {
        return new OfertaFormRecord(nome, semestre, dataInicio, dataFim, professorResponsavelId);
    }
}

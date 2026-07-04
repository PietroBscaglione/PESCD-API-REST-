package br.ufscar.dc.dsw.PESCD.dtos;

import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentacaoDocenciaForm {
    private String nomeInstituicao;
    private String nomeDisciplina;
    private String cursoDisciplina;
    private Integer cargaHoraria;
    private LocalDateTime enviadoEm;
    private StatusAlunoOferta status;
    private UUID ofertaId;
    private String ofertaNome;
    private String ofertaSemestre;
    private LocalDate ofertaDataInicio;
    private LocalDate ofertaDataFim;
    private StatusOferta ofertaStatus;
    private String ofertaProfessorResponsavel;

    public String getNomeInstituicao() {
        return nomeInstituicao;
    }

    public void setNomeInstituicao(String nomeInstituicao) {
        this.nomeInstituicao = nomeInstituicao;
    }

    public String getNomeDisciplina() {
        return nomeDisciplina;
    }

    public void setNomeDisciplina(String nomeDisciplina) {
        this.nomeDisciplina = nomeDisciplina;
    }

    public String getCursoDisciplina() {
        return cursoDisciplina;
    }

    public void setCursoDisciplina(String cursoDisciplina) {
        this.cursoDisciplina = cursoDisciplina;
    }

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public LocalDateTime getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(LocalDateTime enviadoEm) {
        this.enviadoEm = enviadoEm;
    }

    public StatusAlunoOferta getStatus() {
        return status;
    }

    public void setStatus(StatusAlunoOferta status) {
        this.status = status;
    }

    public UUID getOfertaId() {
        return ofertaId;
    }

    public void setOfertaId(UUID ofertaId) {
        this.ofertaId = ofertaId;
    }

    public String getOfertaNome() {
        return ofertaNome;
    }

    public void setOfertaNome(String ofertaNome) {
        this.ofertaNome = ofertaNome;
    }

    public String getOfertaSemestre() {
        return ofertaSemestre;
    }

    public void setOfertaSemestre(String ofertaSemestre) {
        this.ofertaSemestre = ofertaSemestre;
    }

    public LocalDate getOfertaDataInicio() {
        return ofertaDataInicio;
    }

    public void setOfertaDataInicio(LocalDate ofertaDataInicio) {
        this.ofertaDataInicio = ofertaDataInicio;
    }

    public LocalDate getOfertaDataFim() {
        return ofertaDataFim;
    }

    public void setOfertaDataFim(LocalDate ofertaDataFim) {
        this.ofertaDataFim = ofertaDataFim;
    }

    public StatusOferta getOfertaStatus() {
        return ofertaStatus;
    }

    public void setOfertaStatus(StatusOferta ofertaStatus) {
        this.ofertaStatus = ofertaStatus;
    }

    public String getOfertaProfessorResponsavel() {
        return ofertaProfessorResponsavel;
    }

    public void setOfertaProfessorResponsavel(String ofertaProfessorResponsavel) {
        this.ofertaProfessorResponsavel = ofertaProfessorResponsavel;
    }
}

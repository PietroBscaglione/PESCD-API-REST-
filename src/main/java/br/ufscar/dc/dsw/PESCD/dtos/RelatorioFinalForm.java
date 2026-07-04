package br.ufscar.dc.dsw.PESCD.dtos;

import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RelatorioFinalForm {
    private Integer frequenciaInformada;
    private LocalDateTime enviadoEm;
    private StatusAlunoOferta status;
    private List<HistoricoStatusDto> historicoStatus;
    private UUID ofertaId;
    private String ofertaNome;
    private String ofertaSemestre;
    private LocalDate ofertaDataInicio;
    private LocalDate ofertaDataFim;
    private StatusOferta ofertaStatus;
    private String ofertaProfessorResponsavel;

    public Integer getFrequenciaInformada() {
        return frequenciaInformada;
    }

    public void setFrequenciaInformada(Integer frequenciaInformada) {
        this.frequenciaInformada = frequenciaInformada;
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

    public List<HistoricoStatusDto> getHistoricoStatus() {
        return historicoStatus;
    }

    public void setHistoricoStatus(List<HistoricoStatusDto> historicoStatus) {
        this.historicoStatus = historicoStatus;
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

    public record HistoricoStatusDto(
            StatusAlunoOferta statusAnterior,
            StatusAlunoOferta statusNovo,
            LocalDateTime alteradoEm,
            String observacao
    ) {
    }
}

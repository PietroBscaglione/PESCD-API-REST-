package br.ufscar.dc.dsw.PESCD.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_RELATORIO_FINAL")
public class RelatorioFinalModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "aluno_oferta_id", nullable = false, unique = true)
    private AlunoOfertaModel alunoOferta;

    @Column(nullable = false)
    private Integer frequenciaInformada;

    @Column(nullable = false)
    private String arquivoRelatorio;

    @Column(nullable = false)
    private LocalDateTime enviadoEm;

    @OneToOne(mappedBy = "relatorioFinal", cascade = CascadeType.ALL)
    private AprovacaoRelatorioSupervisorModel aprovacaoSupervisor;

    @OneToOne(mappedBy = "relatorioFinal", cascade = CascadeType.ALL)
    private ConclusaoRelatorioResponsavelModel conclusaoResponsavel;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public AlunoOfertaModel getAlunoOferta() {
        return alunoOferta;
    }

    public void setAlunoOferta(AlunoOfertaModel alunoOferta) {
        this.alunoOferta = alunoOferta;
    }

    public Integer getFrequenciaInformada() {
        return frequenciaInformada;
    }

    public void setFrequenciaInformada(Integer frequenciaInformada) {
        this.frequenciaInformada = frequenciaInformada;
    }

    public String getArquivoRelatorio() {
        return arquivoRelatorio;
    }

    public void setArquivoRelatorio(String arquivoRelatorio) {
        this.arquivoRelatorio = arquivoRelatorio;
    }

    public LocalDateTime getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(LocalDateTime enviadoEm) {
        this.enviadoEm = enviadoEm;
    }

    public AprovacaoRelatorioSupervisorModel getAprovacaoSupervisor() {
        return aprovacaoSupervisor;
    }

    public void setAprovacaoSupervisor(AprovacaoRelatorioSupervisorModel aprovacaoSupervisor) {
        this.aprovacaoSupervisor = aprovacaoSupervisor;
    }

    public ConclusaoRelatorioResponsavelModel getConclusaoResponsavel() {
        return conclusaoResponsavel;
    }

    public void setConclusaoResponsavel(ConclusaoRelatorioResponsavelModel conclusaoResponsavel) {
        this.conclusaoResponsavel = conclusaoResponsavel;
    }
}

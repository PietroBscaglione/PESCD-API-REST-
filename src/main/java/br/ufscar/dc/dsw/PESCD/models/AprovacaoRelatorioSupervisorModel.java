package br.ufscar.dc.dsw.PESCD.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_APROVACAO_RELATORIO_SUPERVISOR")
public class AprovacaoRelatorioSupervisorModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "relatorio_final_id", nullable = false, unique = true)
    private RelatorioFinalModel relatorioFinal;

    @ManyToOne
    @JoinColumn(name = "aprovado_por_id", nullable = false)
    private UsuarioModel aprovadoPor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String parecer;

    @Column(nullable = false)
    private Integer frequencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nota sugestaoNota;

    @Column(nullable = false)
    private LocalDateTime aprovadoEm;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public RelatorioFinalModel getRelatorioFinal() {
        return relatorioFinal;
    }

    public void setRelatorioFinal(RelatorioFinalModel relatorioFinal) {
        this.relatorioFinal = relatorioFinal;
    }

    public UsuarioModel getAprovadoPor() {
        return aprovadoPor;
    }

    public void setAprovadoPor(UsuarioModel aprovadoPor) {
        this.aprovadoPor = aprovadoPor;
    }

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

    public LocalDateTime getAprovadoEm() {
        return aprovadoEm;
    }

    public void setAprovadoEm(LocalDateTime aprovadoEm) {
        this.aprovadoEm = aprovadoEm;
    }
}

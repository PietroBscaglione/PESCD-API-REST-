package br.ufscar.dc.dsw.PESCD.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_CONCLUSAO_RELATORIO_RESPONSAVEL")
public class ConclusaoRelatorioResponsavelModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "relatorio_final_id", nullable = false, unique = true)
    private RelatorioFinalModel relatorioFinal;

    @ManyToOne
    @JoinColumn(name = "concluido_por_id", nullable = false)
    private UsuarioModel concluidoPor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String parecer;

    @Column(nullable = false)
    private Integer frequencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nota nota;

    @Column(nullable = false)
    private LocalDateTime concluidoEm;

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

    public UsuarioModel getConcluidoPor() {
        return concluidoPor;
    }

    public void setConcluidoPor(UsuarioModel concluidoPor) {
        this.concluidoPor = concluidoPor;
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

    public Nota getNota() {
        return nota;
    }

    public void setNota(Nota nota) {
        this.nota = nota;
    }

    public LocalDateTime getConcluidoEm() {
        return concluidoEm;
    }

    public void setConcluidoEm(LocalDateTime concluidoEm) {
        this.concluidoEm = concluidoEm;
    }
}

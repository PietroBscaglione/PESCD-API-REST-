package br.ufscar.dc.dsw.PESCD.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_LOG_STATUS_ALUNO_OFERTA")
public class LogStatusAlunoOfertaModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "aluno_oferta_id", nullable = false)
    private AlunoOfertaModel alunoOferta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAlunoOferta statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAlunoOferta statusNovo;

    @ManyToOne
    @JoinColumn(name = "alterado_por_id")
    private UsuarioModel alteradoPor;

    @Column(nullable = false)
    private LocalDateTime alteradoEm;

    @Column(columnDefinition = "TEXT")
    private String observacao;

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

    public StatusAlunoOferta getStatusAnterior() {
        return statusAnterior;
    }

    public void setStatusAnterior(StatusAlunoOferta statusAnterior) {
        this.statusAnterior = statusAnterior;
    }

    public StatusAlunoOferta getStatusNovo() {
        return statusNovo;
    }

    public void setStatusNovo(StatusAlunoOferta statusNovo) {
        this.statusNovo = statusNovo;
    }

    public UsuarioModel getAlteradoPor() {
        return alteradoPor;
    }

    public void setAlteradoPor(UsuarioModel alteradoPor) {
        this.alteradoPor = alteradoPor;
    }

    public LocalDateTime getAlteradoEm() {
        return alteradoEm;
    }

    public void setAlteradoEm(LocalDateTime alteradoEm) {
        this.alteradoEm = alteradoEm;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
}

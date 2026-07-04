package br.ufscar.dc.dsw.PESCD.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_APROVACAO_PLANO")
public class AprovacaoPlanoModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "plano_trabalho_id", nullable = false, unique = true)
    private PlanoTrabalhoModel planoTrabalho;

    @ManyToOne
    @JoinColumn(name = "aprovado_por_id", nullable = false)
    private UsuarioModel aprovadoPor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String parecer;

    @Column(nullable = false)
    private LocalDateTime aprovadoEm;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public PlanoTrabalhoModel getPlanoTrabalho() {
        return planoTrabalho;
    }

    public void setPlanoTrabalho(PlanoTrabalhoModel planoTrabalho) {
        this.planoTrabalho = planoTrabalho;
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

    public LocalDateTime getAprovadoEm() {
        return aprovadoEm;
    }

    public void setAprovadoEm(LocalDateTime aprovadoEm) {
        this.aprovadoEm = aprovadoEm;
    }
}

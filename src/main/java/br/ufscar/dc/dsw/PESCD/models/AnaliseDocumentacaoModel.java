package br.ufscar.dc.dsw.PESCD.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_ANALISE_DOCUMENTACAO")
public class AnaliseDocumentacaoModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "documentacao_docencia_id", nullable = false, unique = true)
    private DocumentacaoDocenciaModel documentacaoDocencia;

    @ManyToOne
    @JoinColumn(name = "analisado_por_id", nullable = false)
    private UsuarioModel analisadoPor;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String parecer;

    @Column(nullable = false)
    private Integer frequencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nota nota;

    @Column(nullable = false)
    private LocalDateTime analisadoEm;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public DocumentacaoDocenciaModel getDocumentacaoDocencia() {
        return documentacaoDocencia;
    }

    public void setDocumentacaoDocencia(DocumentacaoDocenciaModel documentacaoDocencia) {
        this.documentacaoDocencia = documentacaoDocencia;
    }

    public UsuarioModel getAnalisadoPor() {
        return analisadoPor;
    }

    public void setAnalisadoPor(UsuarioModel analisadoPor) {
        this.analisadoPor = analisadoPor;
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

    public LocalDateTime getAnalisadoEm() {
        return analisadoEm;
    }

    public void setAnalisadoEm(LocalDateTime analisadoEm) {
        this.analisadoEm = analisadoEm;
    }
}

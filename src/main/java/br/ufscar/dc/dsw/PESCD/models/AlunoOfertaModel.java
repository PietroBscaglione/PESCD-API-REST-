package br.ufscar.dc.dsw.PESCD.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "TB_ALUNO_OFERTA",
        uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "oferta_id"})
)
public class AlunoOfertaModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private UsuarioModel aluno;

    @ManyToOne
    @JoinColumn(name = "oferta_id", nullable = false)
    private OfertaModel oferta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAlunoOferta status = StatusAlunoOferta.NAO_ENVIADO;

    @Enumerated(EnumType.STRING)
    private TipoCredito tipoCredito;

    private Integer frequenciaFinal;

    @Enumerated(EnumType.STRING)
    private Nota notaFinal;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(mappedBy = "alunoOferta", cascade = CascadeType.ALL)
    private PlanoTrabalhoModel planoTrabalho;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(mappedBy = "alunoOferta", cascade = CascadeType.ALL)
    private DocumentacaoDocenciaModel documentacaoDocencia;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToOne(mappedBy = "alunoOferta", cascade = CascadeType.ALL)
    private RelatorioFinalModel relatorioFinal;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "alunoOferta", cascade = CascadeType.ALL)
    private Set<LogStatusAlunoOfertaModel> logsStatus = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UsuarioModel getAluno() {
        return aluno;
    }

    public void setAluno(UsuarioModel aluno) {
        this.aluno = aluno;
    }

    public OfertaModel getOferta() {
        return oferta;
    }

    public void setOferta(OfertaModel oferta) {
        this.oferta = oferta;
    }

    public StatusAlunoOferta getStatus() {
        return status;
    }

    public void setStatus(StatusAlunoOferta status) {
        this.status = status;
    }

    public TipoCredito getTipoCredito() {
        return tipoCredito;
    }

    public void setTipoCredito(TipoCredito tipoCredito) {
        this.tipoCredito = tipoCredito;
    }

    public Integer getFrequenciaFinal() {
        return frequenciaFinal;
    }

    public void setFrequenciaFinal(Integer frequenciaFinal) {
        this.frequenciaFinal = frequenciaFinal;
    }

    public Nota getNotaFinal() {
        return notaFinal;
    }

    public void setNotaFinal(Nota notaFinal) {
        this.notaFinal = notaFinal;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public PlanoTrabalhoModel getPlanoTrabalho() {
        return planoTrabalho;
    }

    public void setPlanoTrabalho(PlanoTrabalhoModel planoTrabalho) {
        this.planoTrabalho = planoTrabalho;
    }

    public DocumentacaoDocenciaModel getDocumentacaoDocencia() {
        return documentacaoDocencia;
    }

    public void setDocumentacaoDocencia(DocumentacaoDocenciaModel documentacaoDocencia) {
        this.documentacaoDocencia = documentacaoDocencia;
    }

    public RelatorioFinalModel getRelatorioFinal() {
        return relatorioFinal;
    }

    public void setRelatorioFinal(RelatorioFinalModel relatorioFinal) {
        this.relatorioFinal = relatorioFinal;
    }

    public Set<LogStatusAlunoOfertaModel> getLogsStatus() {
        return logsStatus;
    }

    public void setLogsStatus(Set<LogStatusAlunoOfertaModel> logsStatus) {
        this.logsStatus = logsStatus;
    }
}

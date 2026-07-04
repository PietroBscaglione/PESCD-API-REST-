package br.ufscar.dc.dsw.PESCD.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "TB_OFERTA")
public class OfertaModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String semestre;

    @Column(nullable = false)
    private LocalDate dataInicio;

    @Column(nullable = false)
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusOferta status = StatusOferta.EM_ANDAMENTO;

    @Column(nullable = false)
    private LocalDateTime criadoEm;

    private LocalDateTime encerradoResponsavelEm;

    private LocalDateTime encerradoSecretarioEm;

    @Column(columnDefinition = "TEXT")
    private String licoesAprendidas;

    @ManyToOne
    @JoinColumn(name = "professor_responsavel_id", nullable = false)
    private UsuarioModel professorResponsavel;

    @ManyToOne
    @JoinColumn(name = "criado_por_id", nullable = false)
    private UsuarioModel criadoPor;

    @ManyToOne
    @JoinColumn(name = "encerrado_secretario_por_id")
    private UsuarioModel encerradoSecretarioPor;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "oferta", cascade = CascadeType.ALL)
    private Set<AlunoOfertaModel> alunos = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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

    public StatusOferta getStatus() {
        return status;
    }

    public void setStatus(StatusOferta status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getEncerradoResponsavelEm() {
        return encerradoResponsavelEm;
    }

    public void setEncerradoResponsavelEm(LocalDateTime encerradoResponsavelEm) {
        this.encerradoResponsavelEm = encerradoResponsavelEm;
    }

    public LocalDateTime getEncerradoSecretarioEm() {
        return encerradoSecretarioEm;
    }

    public void setEncerradoSecretarioEm(LocalDateTime encerradoSecretarioEm) {
        this.encerradoSecretarioEm = encerradoSecretarioEm;
    }

    public String getLicoesAprendidas() {
        return licoesAprendidas;
    }

    public void setLicoesAprendidas(String licoesAprendidas) {
        this.licoesAprendidas = licoesAprendidas;
    }

    public UsuarioModel getProfessorResponsavel() {
        return professorResponsavel;
    }

    public void setProfessorResponsavel(UsuarioModel professorResponsavel) {
        this.professorResponsavel = professorResponsavel;
    }

    public UsuarioModel getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(UsuarioModel criadoPor) {
        this.criadoPor = criadoPor;
    }

    public UsuarioModel getEncerradoSecretarioPor() {
        return encerradoSecretarioPor;
    }

    public void setEncerradoSecretarioPor(UsuarioModel encerradoSecretarioPor) {
        this.encerradoSecretarioPor = encerradoSecretarioPor;
    }

    public Set<AlunoOfertaModel> getAlunos() {
        return alunos;
    }

    public void setAlunos(Set<AlunoOfertaModel> alunos) {
        this.alunos = alunos;
    }
}

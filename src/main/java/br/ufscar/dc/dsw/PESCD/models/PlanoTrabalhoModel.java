package br.ufscar.dc.dsw.PESCD.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_PLANO_TRABALHO")
public class PlanoTrabalhoModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "aluno_oferta_id", nullable = false, unique = true)
    private AlunoOfertaModel alunoOferta;

    @Column(nullable = false)
    private String codigoDisciplina;

    @Column(nullable = false)
    private String nomeDisciplina;

    @Column(nullable = false)
    private String cursoDisciplina;

    @ManyToOne
    @JoinColumn(name = "professor_supervisor_id", nullable = false)
    private UsuarioModel professorSupervisor;

    @Column(nullable = false)
    private String arquivoPlano;

    @Column(nullable = false)
    private LocalDateTime enviadoEm;

    @OneToOne(mappedBy = "planoTrabalho", cascade = CascadeType.ALL)
    private AprovacaoPlanoModel aprovacaoPlano;

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

    public String getCodigoDisciplina() {
        return codigoDisciplina;
    }

    public void setCodigoDisciplina(String codigoDisciplina) {
        this.codigoDisciplina = codigoDisciplina;
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

    public UsuarioModel getProfessorSupervisor() {
        return professorSupervisor;
    }

    public void setProfessorSupervisor(UsuarioModel professorSupervisor) {
        this.professorSupervisor = professorSupervisor;
    }

    public String getArquivoPlano() {
        return arquivoPlano;
    }

    public void setArquivoPlano(String arquivoPlano) {
        this.arquivoPlano = arquivoPlano;
    }

    public LocalDateTime getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(LocalDateTime enviadoEm) {
        this.enviadoEm = enviadoEm;
    }

    public AprovacaoPlanoModel getAprovacaoPlano() {
        return aprovacaoPlano;
    }

    public void setAprovacaoPlano(AprovacaoPlanoModel aprovacaoPlano) {
        this.aprovacaoPlano = aprovacaoPlano;
    }
}

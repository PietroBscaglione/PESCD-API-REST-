package br.ufscar.dc.dsw.PESCD.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "TB_DOCUMENTACAO_DOCENCIA")
public class DocumentacaoDocenciaModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "aluno_oferta_id", nullable = false, unique = true)
    private AlunoOfertaModel alunoOferta;

    @Column(nullable = false)
    private String nomeInstituicao;

    @Column(nullable = false)
    private String nomeDisciplina;

    @Column(nullable = false)
    private String cursoDisciplina;

    @Column(nullable = false)
    private Integer cargaHoraria;

    @Column(nullable = false)
    private String arquivoDocumentacao;

    @Column(nullable = false)
    private LocalDateTime enviadoEm;

    @OneToOne(mappedBy = "documentacaoDocencia", cascade = CascadeType.ALL)
    private AnaliseDocumentacaoModel analiseDocumentacao;

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

    public String getNomeInstituicao() {
        return nomeInstituicao;
    }

    public void setNomeInstituicao(String nomeInstituicao) {
        this.nomeInstituicao = nomeInstituicao;
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

    public Integer getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(Integer cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    public String getArquivoDocumentacao() {
        return arquivoDocumentacao;
    }

    public void setArquivoDocumentacao(String arquivoDocumentacao) {
        this.arquivoDocumentacao = arquivoDocumentacao;
    }

    public LocalDateTime getEnviadoEm() {
        return enviadoEm;
    }

    public void setEnviadoEm(LocalDateTime enviadoEm) {
        this.enviadoEm = enviadoEm;
    }

    public AnaliseDocumentacaoModel getAnaliseDocumentacao() {
        return analiseDocumentacao;
    }

    public void setAnaliseDocumentacao(AnaliseDocumentacaoModel analiseDocumentacao) {
        this.analiseDocumentacao = analiseDocumentacao;
    }
}

package br.ufscar.dc.dsw.PESCD.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "TB_USUARIO")
public class UsuarioModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String nomeCompleto;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String ra;

    @Column(nullable = false, unique = true, length = 60)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "TB_USUARIO_PERFIL",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "perfil_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "perfil_id"})
    )
    private Set<PerfilModel> perfis = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "professorResponsavel")
    private Set<OfertaModel> ofertasResponsavel = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "aluno")
    private Set<AlunoOfertaModel> matriculas = new HashSet<>();

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @OneToMany(mappedBy = "professorSupervisor")
    private Set<PlanoTrabalhoModel> planosSupervisionados = new HashSet<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<PerfilModel> getPerfis() {
        return perfis;
    }

    public void setPerfis(Set<PerfilModel> perfis) {
        this.perfis = perfis;
    }

    public void adicionarPerfil(PerfilModel perfil) {
        this.perfis.add(perfil);
    }

    public String getNomeUsuario() {
        return username;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.username = nomeUsuario;
    }

    public String getSenha() {
        return password;
    }

    public void setSenha(String senha) {
        this.password = senha;
    }

    public Set<OfertaModel> getOfertasResponsavel() {
        return ofertasResponsavel;
    }

    public void setOfertasResponsavel(Set<OfertaModel> ofertasResponsavel) {
        this.ofertasResponsavel = ofertasResponsavel;
    }

    public Set<AlunoOfertaModel> getMatriculas() {
        return matriculas;
    }

    public void setMatriculas(Set<AlunoOfertaModel> matriculas) {
        this.matriculas = matriculas;
    }

    public Set<PlanoTrabalhoModel> getPlanosSupervisionados() {
        return planosSupervisionados;
    }

    public void setPlanosSupervisionados(Set<PlanoTrabalhoModel> planosSupervisionados) {
        this.planosSupervisionados = planosSupervisionados;
    }
}

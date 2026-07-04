package br.ufscar.dc.dsw.PESCD.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "TB_PERFIL")
public class PerfilModel implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, length = 40)
    private PerfilUsuario nome;

    public PerfilModel() {
    }

    public PerfilModel(PerfilUsuario nome) {
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PerfilUsuario getNome() {
        return nome;
    }

    public void setNome(PerfilUsuario nome) {
        this.nome = nome;
    }

    public String getAuthority() {
        return nome.name();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PerfilModel perfilModel)) {
            return false;
        }
        return nome == perfilModel.nome;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}

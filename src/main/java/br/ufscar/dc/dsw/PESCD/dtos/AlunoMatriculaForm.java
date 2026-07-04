package br.ufscar.dc.dsw.PESCD.dtos;

public class AlunoMatriculaForm {
    private String ra;
    private String nomeCompleto;
    private String email;

    public String getRa() {
        return ra;
    }

    public void setRa(String ra) {
        this.ra = ra;
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

    public AlunoMatriculaFormRecord toRecord() {
        return new AlunoMatriculaFormRecord(ra, nomeCompleto, email);
    }
}

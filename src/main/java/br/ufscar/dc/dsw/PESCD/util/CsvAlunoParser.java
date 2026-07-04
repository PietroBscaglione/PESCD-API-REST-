package br.ufscar.dc.dsw.PESCD.util;

import br.ufscar.dc.dsw.PESCD.exception.CsvInvalidoException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class CsvAlunoParser {

    private static final String CABECALHO_ESPERADO = "RA,NOME_COMPLETO,EMAIL";

    private CsvAlunoParser() {
    }

    public record LinhaAlunoCsv(String ra, String nomeCompleto, String email) {
    }

    public static List<LinhaAlunoCsv> parse(InputStream inputStream) {
        try (var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            var linhas = new ArrayList<LinhaAlunoCsv>();
            String cabecalho = reader.readLine();
            if (cabecalho == null) {
                throw new CsvInvalidoException("csv.error.vazio");
            }
            if (!normalizarCabecalho(cabecalho).equals(CABECALHO_ESPERADO)) {
                throw new CsvInvalidoException("csv.error.cabecalho");
            }

            String linha;
            int numeroLinha = 1;
            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                if (linha.isBlank()) {
                    continue;
                }
                var campos = linha.split(",", 3);
                if (campos.length < 3) {
                    throw new CsvInvalidoException("csv.error.linha.invalida");
                }
                var ra = campos[0].trim();
                var nome = campos[1].trim();
                var email = campos[2].trim();
                if (ra.isEmpty() || email.isEmpty()) {
                    throw new CsvInvalidoException("csv.error.linha.invalida");
                }
                linhas.add(new LinhaAlunoCsv(ra, nome, email));
            }
            if (linhas.isEmpty()) {
                throw new CsvInvalidoException("csv.error.sem.dados");
            }
            return linhas;
        } catch (IOException ex) {
            throw new CsvInvalidoException("csv.error.leitura");
        }
    }

    private static String normalizarCabecalho(String cabecalho) {
        return cabecalho.replace(" ", "").toUpperCase();
    }
}

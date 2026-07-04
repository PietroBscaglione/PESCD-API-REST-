package br.ufscar.dc.dsw.PESCD.services;

import br.ufscar.dc.dsw.PESCD.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.PESCD.repositories.ConfiguracaoSistemaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ConfiguracaoSistemaService {

    public static final String CHAVE_INSTRUCOES_ENCERRAMENTO = "instrucoes_encerramento_secretario";

    private final ConfiguracaoSistemaRepository configuracaoSistemaRepository;

    public ConfiguracaoSistemaService(ConfiguracaoSistemaRepository configuracaoSistemaRepository) {
        this.configuracaoSistemaRepository = configuracaoSistemaRepository;
    }

    @Transactional(readOnly = true)
    public List<String> obterInstrucoesEncerramento() {
        var configuracao = configuracaoSistemaRepository.findByChave(CHAVE_INSTRUCOES_ENCERRAMENTO)
                .orElseThrow(RecursoNaoEncontradoException::new);
        return Arrays.stream(configuracao.getValor().split("\\r?\\n"))
                .map(String::trim)
                .filter(linha -> !linha.isEmpty())
                .toList();
    }
}

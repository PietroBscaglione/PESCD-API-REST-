package br.ufscar.dc.dsw.PESCD.services;

import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.LogStatusAlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.models.UsuarioModel;
import br.ufscar.dc.dsw.PESCD.repositories.LogStatusAlunoOfertaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LogStatusAlunoOfertaService {

    private final LogStatusAlunoOfertaRepository logStatusAlunoOfertaRepository;

    public LogStatusAlunoOfertaService(LogStatusAlunoOfertaRepository logStatusAlunoOfertaRepository) {
        this.logStatusAlunoOfertaRepository = logStatusAlunoOfertaRepository;
    }

    @Transactional(readOnly = true)
    public List<LogStatusAlunoOfertaModel> listarPorMatricula(UUID alunoOfertaId) {
        return logStatusAlunoOfertaRepository.findByAlunoOfertaIdOrderByAlteradoEmAsc(alunoOfertaId);
    }

    @Transactional
    public void registrar(
            AlunoOfertaModel alunoOferta,
            StatusAlunoOferta statusAnterior,
            StatusAlunoOferta statusNovo,
            UsuarioModel alteradoPor,
            String observacao) {
        var log = new LogStatusAlunoOfertaModel();
        log.setAlunoOferta(alunoOferta);
        log.setStatusAnterior(statusAnterior);
        log.setStatusNovo(statusNovo);
        log.setAlteradoPor(alteradoPor);
        log.setAlteradoEm(LocalDateTime.now());
        log.setObservacao(observacao);
        logStatusAlunoOfertaRepository.save(log);
    }
}

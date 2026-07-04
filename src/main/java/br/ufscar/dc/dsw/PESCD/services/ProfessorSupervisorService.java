package br.ufscar.dc.dsw.PESCD.services;

import br.ufscar.dc.dsw.PESCD.dtos.AprovarPlanoSupervisorForm;
import br.ufscar.dc.dsw.PESCD.dtos.AprovarRelatorioSupervisorForm;
import br.ufscar.dc.dsw.PESCD.dtos.OfertaSupervisionadaDto;
import br.ufscar.dc.dsw.PESCD.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.AprovacaoPlanoModel;
import br.ufscar.dc.dsw.PESCD.models.AprovacaoRelatorioSupervisorModel;
import br.ufscar.dc.dsw.PESCD.models.LogStatusAlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.repositories.AlunoOfertaRepository;
import br.ufscar.dc.dsw.PESCD.repositories.AprovacaoPlanoRepository;
import br.ufscar.dc.dsw.PESCD.repositories.AprovacaoRelatorioSupervisorRepository;
import br.ufscar.dc.dsw.PESCD.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

@Service
public class ProfessorSupervisorService {

    private final AlunoOfertaRepository alunoOfertaRepository;
    private final AprovacaoPlanoRepository aprovacaoPlanoRepository;
    private final AprovacaoRelatorioSupervisorRepository aprovacaoRelatorioSupervisorRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogStatusAlunoOfertaService logStatusAlunoOfertaService;

    public ProfessorSupervisorService(
            AlunoOfertaRepository alunoOfertaRepository,
            AprovacaoPlanoRepository aprovacaoPlanoRepository,
            AprovacaoRelatorioSupervisorRepository aprovacaoRelatorioSupervisorRepository,
            UsuarioRepository usuarioRepository,
            LogStatusAlunoOfertaService logStatusAlunoOfertaService) {
        this.alunoOfertaRepository = alunoOfertaRepository;
        this.aprovacaoPlanoRepository = aprovacaoPlanoRepository;
        this.aprovacaoRelatorioSupervisorRepository = aprovacaoRelatorioSupervisorRepository;
        this.usuarioRepository = usuarioRepository;
        this.logStatusAlunoOfertaService = logStatusAlunoOfertaService;
    }

    @Transactional(readOnly = true)
    public List<OfertaSupervisionadaDto> listarSupervisoes(String username) {
        var matriculas = alunoOfertaRepository.findSupervisionadasBySupervisorUsername(username);
        var agrupadas = new LinkedHashMap<UUID, OfertaSupervisionadaDto>();

        for (var matricula : matriculas) {
            var oferta = matricula.getOferta();
            agrupadas.computeIfAbsent(
                    oferta.getId(),
                    id -> new OfertaSupervisionadaDto(oferta, new ArrayList<>()));
            agrupadas.get(oferta.getId()).alunos().add(matricula);
        }

        return List.copyOf(agrupadas.values());
    }

    @Transactional(readOnly = true)
    public AlunoOfertaModel buscarParaAprovarPlano(String username, UUID matriculaId) {
        var matricula = buscarMatriculaSupervisionada(username, matriculaId);
        if (matricula.getStatus() != StatusAlunoOferta.PLANO_ENVIADO) {
            throw new ValidacaoNegocioException("supervisor.error.plano.status.invalido");
        }
        if (matricula.getPlanoTrabalho() == null) {
            throw new ValidacaoNegocioException("supervisor.error.plano.inexistente");
        }
        if (matricula.getPlanoTrabalho().getAprovacaoPlano() != null) {
            throw new ValidacaoNegocioException("supervisor.error.plano.ja.aprovado");
        }
        return matricula;
    }

    @Transactional(readOnly = true)
    public AlunoOfertaModel buscarParaAprovarRelatorio(String username, UUID matriculaId) {
        var matricula = buscarMatriculaSupervisionada(username, matriculaId);
        if (matricula.getStatus() != StatusAlunoOferta.RELATORIO_ENVIADO) {
            throw new ValidacaoNegocioException("supervisor.error.relatorio.status.invalido");
        }
        if (matricula.getPlanoTrabalho() == null) {
            throw new ValidacaoNegocioException("supervisor.error.plano.inexistente");
        }
        if (matricula.getRelatorioFinal() == null) {
            throw new ValidacaoNegocioException("supervisor.error.relatorio.inexistente");
        }
        if (matricula.getRelatorioFinal().getAprovacaoSupervisor() != null) {
            throw new ValidacaoNegocioException("supervisor.error.relatorio.ja.aprovado");
        }
        return matricula;
    }

    @Transactional(readOnly = true)
    public List<LogStatusAlunoOfertaModel> listarHistorico(UUID matriculaId) {
        return logStatusAlunoOfertaService.listarPorMatricula(matriculaId);
    }

    @Transactional
    public void aprovarPlano(String username, UUID matriculaId, AprovarPlanoSupervisorForm form) {
        var supervisor = usuarioRepository.findByUsername(username).orElseThrow(RecursoNaoEncontradoException::new);
        var matricula = buscarParaAprovarPlano(username, matriculaId);
        var plano = matricula.getPlanoTrabalho();

        var aprovacao = new AprovacaoPlanoModel();
        aprovacao.setPlanoTrabalho(plano);
        aprovacao.setAprovadoPor(supervisor);
        aprovacao.setParecer(form.getParecer().trim());
        aprovacao.setAprovadoEm(LocalDateTime.now());

        var statusAnterior = matricula.getStatus();
        matricula.setStatus(StatusAlunoOferta.PLANO_APROVADO);
        aprovacaoPlanoRepository.save(aprovacao);
        alunoOfertaRepository.save(matricula);
        logStatusAlunoOfertaService.registrar(
                matricula,
                statusAnterior,
                StatusAlunoOferta.PLANO_APROVADO,
                supervisor,
                "Plano de trabalho aprovado pelo professor supervisor.");
    }

    @Transactional
    public void aprovarRelatorio(String username, UUID matriculaId, AprovarRelatorioSupervisorForm form) {
        var supervisor = usuarioRepository.findByUsername(username).orElseThrow(RecursoNaoEncontradoException::new);
        var matricula = buscarParaAprovarRelatorio(username, matriculaId);
        var relatorio = matricula.getRelatorioFinal();

        var aprovacao = new AprovacaoRelatorioSupervisorModel();
        aprovacao.setRelatorioFinal(relatorio);
        aprovacao.setAprovadoPor(supervisor);
        aprovacao.setParecer(form.getParecer().trim());
        aprovacao.setFrequencia(form.getFrequencia());
        aprovacao.setSugestaoNota(form.getSugestaoNota());
        aprovacao.setAprovadoEm(LocalDateTime.now());

        var statusAnterior = matricula.getStatus();
        matricula.setStatus(StatusAlunoOferta.RELATORIO_APROVADO_PELO_SUPERVISOR);
        aprovacaoRelatorioSupervisorRepository.save(aprovacao);
        alunoOfertaRepository.save(matricula);
        logStatusAlunoOfertaService.registrar(
                matricula,
                statusAnterior,
                StatusAlunoOferta.RELATORIO_APROVADO_PELO_SUPERVISOR,
                supervisor,
                "Relatorio final aprovado pelo professor supervisor.");
    }

    @Transactional(readOnly = true)
    public AlunoOfertaModel buscarMatriculaSupervisionada(String username, UUID matriculaId) {
        return alunoOfertaRepository.findSupervisionadaByIdAndSupervisorUsername(matriculaId, username)
                .orElseThrow(RecursoNaoEncontradoException::new);
    }
}

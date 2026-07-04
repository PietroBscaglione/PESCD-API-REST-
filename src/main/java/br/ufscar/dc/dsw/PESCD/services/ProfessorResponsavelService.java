package br.ufscar.dc.dsw.PESCD.services;

import br.ufscar.dc.dsw.PESCD.dtos.OfertaListagemDto;
import br.ufscar.dc.dsw.PESCD.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.AnaliseDocumentacaoModel;
import br.ufscar.dc.dsw.PESCD.models.ConclusaoRelatorioResponsavelModel;
import br.ufscar.dc.dsw.PESCD.models.LogStatusAlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.Nota;
import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOfertaExibicao;
import br.ufscar.dc.dsw.PESCD.repositories.AnaliseDocumentacaoRepository;
import br.ufscar.dc.dsw.PESCD.repositories.ConclusaoRelatorioResponsavelRepository;
import br.ufscar.dc.dsw.PESCD.repositories.ProfessorResponsavelAlunoOfertaRepository;
import br.ufscar.dc.dsw.PESCD.repositories.ProfessorResponsavelOfertaRepository;
import br.ufscar.dc.dsw.PESCD.repositories.UsuarioRepository;
import br.ufscar.dc.dsw.PESCD.util.StatusOfertaResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.UUID;

@Service
public class ProfessorResponsavelService {

    private final ProfessorResponsavelOfertaRepository ofertaRepository;
    private final ProfessorResponsavelAlunoOfertaRepository alunoOfertaRepository;
    private final ConclusaoRelatorioResponsavelRepository conclusaoRelatorioRepository;
    private final AnaliseDocumentacaoRepository analiseDocumentacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogStatusAlunoOfertaService logStatusAlunoOfertaService;

    public ProfessorResponsavelService(
            ProfessorResponsavelOfertaRepository ofertaRepository,
            ProfessorResponsavelAlunoOfertaRepository alunoOfertaRepository,
            ConclusaoRelatorioResponsavelRepository conclusaoRelatorioRepository,
            AnaliseDocumentacaoRepository analiseDocumentacaoRepository,
            UsuarioRepository usuarioRepository,
            LogStatusAlunoOfertaService logStatusAlunoOfertaService) {
        this.ofertaRepository = ofertaRepository;
        this.alunoOfertaRepository = alunoOfertaRepository;
        this.conclusaoRelatorioRepository = conclusaoRelatorioRepository;
        this.analiseDocumentacaoRepository = analiseDocumentacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.logStatusAlunoOfertaService = logStatusAlunoOfertaService;
    }

    @Transactional(readOnly = true)
    public List<OfertaModel> listarOfertasDoResponsavel(String username) {
        return ofertaRepository.findByProfessorResponsavelUsernameOrderBySemestreDescDataInicioDescNomeAsc(username);
    }

    @Transactional(readOnly = true)
    public List<OfertaListagemDto> listarOfertasParaAcompanhamento(String username) {
        var hoje = LocalDate.now();
        return listarOfertasDoResponsavel(username).stream()
                .map(oferta -> toListagemDto(oferta, hoje))
                .toList();
    }

    @Transactional(readOnly = true)
    public OfertaModel buscarOfertaDoResponsavel(String username, UUID ofertaId) {
        return ofertaRepository.findByIdAndProfessorResponsavelUsername(ofertaId, username)
                .orElseThrow(RecursoNaoEncontradoException::new);
    }

    @Transactional(readOnly = true)
    public OfertaListagemDto buscarResumoOferta(String username, UUID ofertaId) {
        return toListagemDto(buscarOfertaDoResponsavel(username, ofertaId), LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<AlunoOfertaModel> listarAlunosDaOferta(String username, UUID ofertaId) {
        return alunoOfertaRepository.findByOfertaIdAndOfertaProfessorResponsavelUsernameOrderByAlunoNomeCompletoAsc(
                ofertaId,
                username);
    }

    @Transactional(readOnly = true)
    public AlunoOfertaModel buscarParaConcluirRelatorio(String username, UUID matriculaId) {
        var matricula = buscarMatriculaDoResponsavel(username, matriculaId);
        if (matricula.getStatus() != StatusAlunoOferta.RELATORIO_APROVADO_PELO_SUPERVISOR) {
            throw new ValidacaoNegocioException("responsavel.error.relatorio.status.invalido");
        }
        if (matricula.getPlanoTrabalho() == null) {
            throw new ValidacaoNegocioException("responsavel.error.plano.inexistente");
        }
        if (matricula.getRelatorioFinal() == null) {
            throw new ValidacaoNegocioException("responsavel.error.relatorio.inexistente");
        }
        if (matricula.getRelatorioFinal().getConclusaoResponsavel() != null
                || conclusaoRelatorioRepository.findByRelatorioFinalId(matricula.getRelatorioFinal().getId()) != null) {
            throw new ValidacaoNegocioException("responsavel.error.relatorio.ja.concluido");
        }
        return matricula;
    }

    @Transactional(readOnly = true)
    public List<LogStatusAlunoOfertaModel> listarHistorico(UUID matriculaId) {
        return logStatusAlunoOfertaService.listarPorMatricula(matriculaId);
    }

    @Transactional(readOnly = true)
    public AlunoOfertaModel buscarDetalhesMatricula(String username, UUID matriculaId) {
        return buscarMatriculaDoResponsavel(username, matriculaId);
    }

    @Transactional
    public void concluirRelatorio(
            String username,
            UUID matriculaId,
            String parecer,
            Integer frequenciaFinal,
            Nota notaFinal) {
        validarFormularioConclusao(parecer, frequenciaFinal, notaFinal);
        var responsavel = usuarioRepository.findByUsername(username).orElseThrow(RecursoNaoEncontradoException::new);
        var matricula = buscarParaConcluirRelatorio(username, matriculaId);

        var conclusao = new ConclusaoRelatorioResponsavelModel();
        conclusao.setRelatorioFinal(matricula.getRelatorioFinal());
        conclusao.setConcluidoPor(responsavel);
        conclusao.setParecer(parecer.trim());
        conclusao.setFrequencia(frequenciaFinal);
        conclusao.setNota(notaFinal);
        conclusao.setConcluidoEm(LocalDateTime.now());

        concluirMatricula(
                matricula,
                frequenciaFinal,
                notaFinal,
                "Relatorio final concluido pelo professor responsavel.",
                responsavel);
        conclusaoRelatorioRepository.save(conclusao);
    }

    @Transactional(readOnly = true)
    public AlunoOfertaModel buscarParaAnalisarDocumentacao(String username, UUID matriculaId) {
        var matricula = buscarMatriculaDoResponsavel(username, matriculaId);
        if (matricula.getStatus() != StatusAlunoOferta.DOCUMENTACAO_ENVIADA) {
            throw new ValidacaoNegocioException("responsavel.error.documentacao.status.invalido");
        }
        if (matricula.getDocumentacaoDocencia() == null) {
            throw new ValidacaoNegocioException("responsavel.error.documentacao.inexistente");
        }
        if (matricula.getDocumentacaoDocencia().getAnaliseDocumentacao() != null
                || analiseDocumentacaoRepository.findByDocumentacaoDocenciaId(
                        matricula.getDocumentacaoDocencia().getId()) != null) {
            throw new ValidacaoNegocioException("responsavel.error.documentacao.ja.analisada");
        }
        return matricula;
    }

    @Transactional
    public void analisarDocumentacao(
            String username,
            UUID matriculaId,
            String parecer,
            Integer frequenciaFinal,
            Nota notaFinal) {
        validarFormularioConclusao(parecer, frequenciaFinal, notaFinal);
        var responsavel = usuarioRepository.findByUsername(username).orElseThrow(RecursoNaoEncontradoException::new);
        var matricula = buscarParaAnalisarDocumentacao(username, matriculaId);

        var analise = new AnaliseDocumentacaoModel();
        analise.setDocumentacaoDocencia(matricula.getDocumentacaoDocencia());
        analise.setAnalisadoPor(responsavel);
        analise.setParecer(parecer.trim());
        analise.setFrequencia(frequenciaFinal);
        analise.setNota(notaFinal);
        analise.setAnalisadoEm(LocalDateTime.now());

        concluirMatricula(
                matricula,
                frequenciaFinal,
                notaFinal,
                "Documentacao de docencia analisada pelo professor responsavel.",
                responsavel);
        analiseDocumentacaoRepository.save(analise);
    }

    @Transactional(readOnly = true)
    public boolean podeEncerrarOferta(String username, UUID ofertaId) {
        var oferta = buscarOfertaDoResponsavel(username, ofertaId);
        return oferta.getAlunos().stream()
                .allMatch(matricula -> matricula.getStatus() == StatusAlunoOferta.CONCLUIDO_PELO_RESPONSAVEL);
    }

    @Transactional(readOnly = true)
    public EstatisticasOferta calcularEstatisticas(String username, UUID ofertaId) {
        var alunos = listarAlunosDaOferta(username, ofertaId);
        var frequencias = alunos.stream()
                .filter(aluno -> aluno.getFrequenciaFinal() != null)
                .mapToDouble(AlunoOfertaModel::getFrequenciaFinal)
                .summaryStatistics();
        return new EstatisticasOferta(
                alunos.size(),
                frequencias,
                alunos.stream().filter(aluno -> aluno.getTipoCredito() == br.ufscar.dc.dsw.PESCD.models.TipoCredito.ESTAGIO).count(),
                alunos.stream().filter(aluno -> aluno.getTipoCredito() == br.ufscar.dc.dsw.PESCD.models.TipoCredito.DOCUMENTACAO).count(),
                alunos.stream().filter(aluno -> aluno.getNotaFinal() == Nota.A).count(),
                alunos.stream().filter(aluno -> aluno.getNotaFinal() == Nota.B).count(),
                alunos.stream().filter(aluno -> aluno.getNotaFinal() == Nota.C).count(),
                alunos.stream().filter(aluno -> aluno.getNotaFinal() == Nota.D).count(),
                alunos.stream().filter(aluno -> aluno.getNotaFinal() == Nota.E).count());
    }

    @Transactional
    public void encerrarOferta(String username, UUID ofertaId, String licoesAprendidas) {
        if (licoesAprendidas == null || licoesAprendidas.isBlank()) {
            throw new ValidacaoNegocioException("responsavel.error.licoes.obrigatorio");
        }
        var oferta = buscarOfertaDoResponsavel(username, ofertaId);
        if (!podeEncerrarOferta(username, ofertaId)) {
            throw new ValidacaoNegocioException("responsavel.error.oferta.alunos.pendentes");
        }
        if (oferta.getStatus() == StatusOferta.AGUARDANDO_ENCERRAMENTO_SECRETARIO) {
            return;
        }
        var statusExibicao = StatusOfertaResolver.resolver(oferta, LocalDate.now());
        if (statusExibicao != StatusOfertaExibicao.EM_ANDAMENTO
                && statusExibicao != StatusOfertaExibicao.EM_ATRASO) {
            throw new ValidacaoNegocioException("responsavel.error.oferta.status.invalido");
        }

        oferta.setLicoesAprendidas(licoesAprendidas.trim());
        oferta.setEncerradoResponsavelEm(LocalDateTime.now());
        oferta.setStatus(StatusOferta.AGUARDANDO_ENCERRAMENTO_SECRETARIO);
        ofertaRepository.save(oferta);
    }

    private AlunoOfertaModel buscarMatriculaDoResponsavel(String username, UUID matriculaId) {
        return alunoOfertaRepository.findByIdAndOfertaProfessorResponsavelUsername(matriculaId, username)
                .orElseThrow(RecursoNaoEncontradoException::new);
    }

    private OfertaListagemDto toListagemDto(OfertaModel oferta, LocalDate hoje) {
        return new OfertaListagemDto(
                oferta.getId(),
                oferta.getNome(),
                oferta.getSemestre(),
                oferta.getDataInicio(),
                oferta.getDataFim(),
                oferta.getStatus(),
                StatusOfertaResolver.resolver(oferta, hoje),
                oferta.getProfessorResponsavel().getNomeCompleto(),
                podeEncerrarOferta(oferta.getProfessorResponsavel().getUsername(), oferta.getId()));
    }

    private void validarFormularioConclusao(String parecer, Integer frequenciaFinal, Nota notaFinal) {
        if (parecer == null || parecer.isBlank()) {
            throw new ValidacaoNegocioException("responsavel.error.parecer.obrigatorio");
        }
        if (frequenciaFinal == null || frequenciaFinal < 0 || frequenciaFinal > 100) {
            throw new ValidacaoNegocioException("responsavel.error.frequencia.invalida");
        }
        if (notaFinal == null) {
            throw new ValidacaoNegocioException("responsavel.error.nota.obrigatoria");
        }
    }

    private void concluirMatricula(
            AlunoOfertaModel matricula,
            Integer frequenciaFinal,
            Nota notaFinal,
            String observacao,
            br.ufscar.dc.dsw.PESCD.models.UsuarioModel responsavel) {
        var statusAnterior = matricula.getStatus();
        matricula.setFrequenciaFinal(frequenciaFinal);
        matricula.setNotaFinal(notaFinal);
        matricula.setStatus(StatusAlunoOferta.CONCLUIDO_PELO_RESPONSAVEL);
        alunoOfertaRepository.save(matricula);
        logStatusAlunoOfertaService.registrar(
                matricula,
                statusAnterior,
                StatusAlunoOferta.CONCLUIDO_PELO_RESPONSAVEL,
                responsavel,
                observacao);
    }

    public record EstatisticasOferta(
            int totalAlunos,
            DoubleSummaryStatistics frequencias,
            long totalEstagio,
            long totalDocumentacao,
            long totalNotaA,
            long totalNotaB,
            long totalNotaC,
            long totalNotaD,
            long totalNotaE) {
    }
}

package br.ufscar.dc.dsw.PESCD.services;

import br.ufscar.dc.dsw.PESCD.dtos.PlanoTrabalhoForm;
import br.ufscar.dc.dsw.PESCD.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.PerfilUsuario;
import br.ufscar.dc.dsw.PESCD.models.PlanoTrabalhoModel;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import br.ufscar.dc.dsw.PESCD.models.TipoCredito;
import br.ufscar.dc.dsw.PESCD.repositories.AlunoOfertaRepository;
import br.ufscar.dc.dsw.PESCD.repositories.PlanoTrabalhoRepository;
import br.ufscar.dc.dsw.PESCD.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

@Service
public class PlanoTrabalhoService {

    private static final long TAMANHO_MAXIMO_BYTES = 5L * 1024L * 1024L;
    private static final Path DIRETORIO_PLANOS = Paths.get("uploads", "planos");

    private final PlanoTrabalhoRepository planoTrabalhoRepository;
    private final AlunoOfertaRepository alunoOfertaRepository;
    private final UsuarioRepository usuarioRepository;
    private final LogStatusAlunoOfertaService logStatusAlunoOfertaService;

    public PlanoTrabalhoService(
            PlanoTrabalhoRepository planoTrabalhoRepository,
            AlunoOfertaRepository alunoOfertaRepository,
            UsuarioRepository usuarioRepository,
            LogStatusAlunoOfertaService logStatusAlunoOfertaService) {
        this.planoTrabalhoRepository = planoTrabalhoRepository;
        this.alunoOfertaRepository = alunoOfertaRepository;
        this.usuarioRepository = usuarioRepository;
        this.logStatusAlunoOfertaService = logStatusAlunoOfertaService;
    }

    @Transactional(readOnly = true)
    public AlunoOfertaModel buscarMatriculaElegivel(String username, UUID ofertaId) {
        var matricula = alunoOfertaRepository.findByAlunoUsernameAndOfertaId(username, ofertaId)
                .orElseThrow(RecursoNaoEncontradoException::new);
        validarPreCondicoes(matricula);
        return matricula;
    }

    @Transactional(readOnly = true)
    public PlanoTrabalhoForm consultarPlano(String username, UUID ofertaId) {
        var matricula = alunoOfertaRepository.findByAlunoUsernameAndOfertaId(username, ofertaId)
                .orElseThrow(RecursoNaoEncontradoException::new);
        var form = new PlanoTrabalhoForm();
        preencherDadosMatricula(form, matricula);

        var planoOptional = planoTrabalhoRepository.findOptionalByAlunoOfertaId(matricula.getId());
        if (planoOptional.isEmpty()) {
            return form;
        }

        var plano = planoOptional.get();
        form.setCodigoDisciplina(plano.getCodigoDisciplina());
        form.setNomeDisciplina(plano.getNomeDisciplina());
        form.setCursoDisciplina(plano.getCursoDisciplina());
        form.setProfessorSupervisorId(plano.getProfessorSupervisor().getId());
        form.setProfessorSupervisorNome(plano.getProfessorSupervisor().getNomeCompleto());
        form.setEnviadoEm(plano.getEnviadoEm());
        return form;
    }

    @Transactional
    public PlanoTrabalhoModel enviarPlano(String username, UUID ofertaId, PlanoTrabalhoForm form, MultipartFile arquivo) {
        var matricula = alunoOfertaRepository.findByAlunoUsernameAndOfertaId(username, ofertaId)
                .orElseThrow(RecursoNaoEncontradoException::new);
        validarPreCondicoes(matricula);
        validarArquivo(arquivo);

        var supervisor = usuarioRepository.findById(form.getProfessorSupervisorId())
                .orElseThrow(() -> new ValidacaoNegocioException("plano.error.supervisor.obrigatorio"));
        var supervisorValido = supervisor.getPerfis().stream()
                .anyMatch(perfil -> perfil.getNome() == PerfilUsuario.ROLE_SUPERVISOR);
        if (!supervisorValido) {
            throw new ValidacaoNegocioException("plano.error.supervisor.invalido");
        }

        var plano = new PlanoTrabalhoModel();
        plano.setAlunoOferta(matricula);
        plano.setCodigoDisciplina(form.getCodigoDisciplina().trim());
        plano.setNomeDisciplina(form.getNomeDisciplina().trim());
        plano.setCursoDisciplina(form.getCursoDisciplina().trim());
        plano.setProfessorSupervisor(supervisor);
        plano.setArquivoPlano(armazenarArquivo(matricula.getId(), arquivo));
        plano.setEnviadoEm(LocalDateTime.now());

        var statusAnterior = matricula.getStatus();
        matricula.setStatus(StatusAlunoOferta.PLANO_ENVIADO);
        matricula.setTipoCredito(TipoCredito.ESTAGIO);
        var planoSalvo = planoTrabalhoRepository.save(plano);
        alunoOfertaRepository.save(matricula);
        logStatusAlunoOfertaService.registrar(
                matricula,
                statusAnterior,
                StatusAlunoOferta.PLANO_ENVIADO,
                matricula.getAluno(),
                "Plano de trabalho enviado pelo aluno.");

        return planoSalvo;
    }

    private void validarPreCondicoes(AlunoOfertaModel matricula) {
        if (matricula.getOferta().getStatus() != StatusOferta.EM_ANDAMENTO) {
            throw new ValidacaoNegocioException("plano.error.oferta.nao.andamento");
        }
        if (matricula.getStatus() != StatusAlunoOferta.NAO_ENVIADO) {
            throw new ValidacaoNegocioException("plano.error.status.invalido");
        }
        if (planoTrabalhoRepository.findOptionalByAlunoOfertaId(matricula.getId()).isPresent()) {
            throw new ValidacaoNegocioException("plano.error.ja.enviado");
        }
    }

    private void preencherDadosMatricula(PlanoTrabalhoForm form, AlunoOfertaModel matricula) {
        var oferta = matricula.getOferta();
        form.setStatus(matricula.getStatus());
        form.setOfertaId(oferta.getId());
        form.setOfertaNome(oferta.getNome());
        form.setOfertaSemestre(oferta.getSemestre());
        form.setOfertaDataInicio(oferta.getDataInicio());
        form.setOfertaDataFim(oferta.getDataFim());
        form.setOfertaStatus(oferta.getStatus());
        form.setOfertaProfessorResponsavel(oferta.getProfessorResponsavel().getNomeCompleto());
    }

    private void validarArquivo(MultipartFile arquivo) {
        if (arquivo == null || arquivo.isEmpty()) {
            throw new ValidacaoNegocioException("plano.error.arquivo.obrigatorio");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO_BYTES) {
            throw new ValidacaoNegocioException("plano.error.arquivo.tamanho");
        }
        var contentType = arquivo.getContentType();
        var nomeOriginal = arquivo.getOriginalFilename();
        var extensaoPdf = nomeOriginal != null && nomeOriginal.toLowerCase(Locale.ROOT).endsWith(".pdf");
        if (!extensaoPdf || !"application/pdf".equalsIgnoreCase(contentType)) {
            throw new ValidacaoNegocioException("plano.error.arquivo.pdf");
        }
    }

    private String armazenarArquivo(UUID matriculaId, MultipartFile arquivo) {
        try {
            Files.createDirectories(DIRETORIO_PLANOS);
            var destino = DIRETORIO_PLANOS.resolve(matriculaId + "-" + UUID.randomUUID() + ".pdf");
            Files.copy(arquivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return destino.toString();
        } catch (IOException ex) {
            throw new ValidacaoNegocioException("plano.error.arquivo.salvar");
        }
    }
}

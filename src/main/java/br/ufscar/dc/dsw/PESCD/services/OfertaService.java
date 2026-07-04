package br.ufscar.dc.dsw.PESCD.services;

import br.ufscar.dc.dsw.PESCD.dtos.OfertaFormRecord;
import br.ufscar.dc.dsw.PESCD.dtos.OfertaListagemDto;
import br.ufscar.dc.dsw.PESCD.exception.OfertaEncerradaException;
import br.ufscar.dc.dsw.PESCD.exception.RecursoNaoEncontradoException;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import br.ufscar.dc.dsw.PESCD.models.PerfilUsuario;
import br.ufscar.dc.dsw.PESCD.models.StatusAlunoOferta;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import br.ufscar.dc.dsw.PESCD.models.UsuarioModel;
import br.ufscar.dc.dsw.PESCD.repositories.OfertaRepository;
import br.ufscar.dc.dsw.PESCD.util.StatusOfertaResolver;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class OfertaService {

    private final OfertaRepository ofertaRepository;
    private final UsuarioService usuarioService;

    public OfertaService(OfertaRepository ofertaRepository, UsuarioService usuarioService) {
        this.ofertaRepository = ofertaRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<OfertaListagemDto> listarParaAcompanhamento() {
        var hoje = LocalDate.now();
        return ofertaRepository.findByOrderBySemestreDesc().stream()
                .map(oferta -> toListagemDto(oferta, hoje))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OfertaListagemDto> listarOfertasDoAlunoLogado(String username) {
        var hoje = LocalDate.now();
        return ofertaRepository.findOfertasDoAlunoByUsername(username).stream()
                .map(oferta -> toListagemDto(oferta, hoje))
                .toList();
    }

    @Transactional(readOnly = true)
    public OfertaModel buscarPorId(UUID id) {
        return ofertaRepository.findById(id).orElseThrow(RecursoNaoEncontradoException::new);
    }

    @Transactional(readOnly = true)
    public OfertaModel buscarDetalhes(UUID id) {
        return ofertaRepository.findWithDetalhesById(id).orElseThrow(RecursoNaoEncontradoException::new);
    }

    @Transactional(readOnly = true)
    public OfertaListagemDto toListagemDto(OfertaModel oferta) {
        return toListagemDto(oferta, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public ResultadoExportacaoCsv exportarResultadosCsv(UUID id) {
        var oferta = buscarDetalhes(id);
        if (oferta.getStatus() != StatusOferta.CONCLUIDA) {
            throw new ValidacaoNegocioException("oferta.error.exportar.status.invalido");
        }

        var csv = new StringBuilder("RA,NOME_COMPLETO,NOTA,FREQUENCIA,CREDITOS_POR_ESTAGIO\r\n");
        oferta.getAlunos().stream()
                .filter(matricula -> matricula.getStatus() == StatusAlunoOferta.CONCLUIDO_PELO_RESPONSAVEL)
                .sorted(Comparator.comparing(
                        matricula -> matricula.getAluno().getNomeCompleto(),
                        String.CASE_INSENSITIVE_ORDER))
                .forEach(matricula -> csv
                        .append(formatarCampoCsv(matricula.getAluno().getRa())).append(',')
                        .append(formatarCampoCsv(matricula.getAluno().getNomeCompleto())).append(',')
                        .append(formatarCampoCsv(matricula.getNotaFinal())).append(',')
                        .append(formatarCampoCsv(matricula.getFrequenciaFinal())).append(',')
                        .append(formatarCampoCsv(matricula.getTipoCredito()))
                        .append("\r\n"));

        return new ResultadoExportacaoCsv(
                "resultados_%s_%s.csv".formatted(
                        normalizarParaNomeArquivo(oferta.getNome()),
                        normalizarParaNomeArquivo(oferta.getSemestre())),
                csv.toString().getBytes(StandardCharsets.UTF_8));
    }

    @Transactional
    public OfertaModel salvar(OfertaFormRecord form, UsuarioModel secretario) {
        validarDatas(form.dataInicio(), form.dataFim());

        var professor = usuarioService.buscarPorId(form.professorResponsavelId());
        validarProfessor(professor);

        var oferta = new OfertaModel();
        oferta.setSemestre(form.semestre().trim());
        oferta.setNome(resolverNome(form.nome(), form.semestre()));
        oferta.setDataInicio(form.dataInicio());
        oferta.setDataFim(form.dataFim());
        oferta.setProfessorResponsavel(professor);
        oferta.setCriadoPor(secretario);
        oferta.setCriadoEm(LocalDateTime.now());
        oferta.setStatus(StatusOferta.EM_ANDAMENTO);

        return ofertaRepository.save(oferta);
    }

    @Transactional(readOnly = true)
    public boolean podeEncerrar(OfertaModel oferta) {
        return oferta.getStatus() == StatusOferta.AGUARDANDO_ENCERRAMENTO_SECRETARIO;
    }

    @Transactional
    public OfertaModel encerrar(UUID id, UsuarioModel secretario) {
        var oferta = buscarPorId(id);
        if (oferta.getStatus() == StatusOferta.CONCLUIDA) {
            return oferta;
        }
        if (!podeEncerrar(oferta)) {
            throw new ValidacaoNegocioException("oferta.error.encerrar.status.invalido");
        }
        oferta.setStatus(StatusOferta.CONCLUIDA);
        oferta.setEncerradoSecretarioEm(LocalDateTime.now());
        oferta.setEncerradoSecretarioPor(secretario);
        return ofertaRepository.save(oferta);
    }

    public void assertOfertaEditavel(OfertaModel oferta) {
        if (oferta.getStatus() == StatusOferta.CONCLUIDA) {
            throw new OfertaEncerradaException();
        }
    }

    private OfertaListagemDto toListagemDto(OfertaModel oferta, LocalDate hoje) {
        var statusExibicao = StatusOfertaResolver.resolver(oferta, hoje);
        return new OfertaListagemDto(
                oferta.getId(),
                oferta.getNome(),
                oferta.getSemestre(),
                oferta.getDataInicio(),
                oferta.getDataFim(),
                oferta.getStatus(),
                statusExibicao,
                oferta.getProfessorResponsavel().getNomeCompleto(),
                podeEncerrar(oferta)
        );
    }

    private void validarDatas(LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null) {
            throw new ValidacaoNegocioException("oferta.error.datas.obrigatorias");
        }
        if (!fim.isAfter(inicio)) {
            throw new ValidacaoNegocioException("oferta.error.data.fim.invalida");
        }
    }

    private void validarProfessor(UsuarioModel professor) {
        var perfisValidos = professor.getPerfis().stream()
                .anyMatch(p -> p.getNome() == PerfilUsuario.ROLE_RESPONSAVEL);
        if (!perfisValidos) {
            throw new ValidacaoNegocioException("oferta.error.professor.invalido");
        }
    }

    private String resolverNome(String nome, String semestre) {
        if (nome == null || nome.isBlank()) {
            return "PESCD - " + semestre.trim();
        }
        return nome.trim();
    }

    private String formatarCampoCsv(Object valor) {
        if (valor == null) {
            return "";
        }
        var texto = valor.toString();
        if (texto.contains(",") || texto.contains("\"") || texto.contains("\n") || texto.contains("\r")) {
            return "\"" + texto.replace("\"", "\"\"") + "\"";
        }
        return texto;
    }

    private String normalizarParaNomeArquivo(String texto) {
        var normalizado = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "");
        return normalizado.isBlank() ? "oferta" : normalizado;
    }

    public record ResultadoExportacaoCsv(String nomeArquivo, byte[] conteudo) {
    }
}

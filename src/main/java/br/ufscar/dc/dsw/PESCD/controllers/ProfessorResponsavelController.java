package br.ufscar.dc.dsw.PESCD.controllers;

import br.ufscar.dc.dsw.PESCD.dtos.ConclusaoResponsavelForm;
import br.ufscar.dc.dsw.PESCD.dtos.EncerrarOfertaResponsavelForm;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.services.ProfessorResponsavelService;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/responsavel/ofertas")
public class ProfessorResponsavelController {

    private final ProfessorResponsavelService professorResponsavelService;

    public ProfessorResponsavelController(ProfessorResponsavelService professorResponsavelService) {
        this.professorResponsavelService = professorResponsavelService;
    }

    @GetMapping
    public String listarOfertas(
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model) {
        var ofertas = professorResponsavelService.listarOfertasParaAcompanhamento(usuarioAutenticado.getUsername());
        model.addAttribute("ofertas", ofertas);
        model.addAttribute("ofertasVazias", ofertas.isEmpty());
        return "professor/dashboard";
    }

    @GetMapping("/{ofertaId}")
    public String detalharOferta(
            @PathVariable UUID ofertaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model) {
        var oferta = professorResponsavelService.buscarOfertaDoResponsavel(usuarioAutenticado.getUsername(), ofertaId);
        model.addAttribute("oferta", oferta);
        model.addAttribute("ofertaResumo", professorResponsavelService.buscarResumoOferta(usuarioAutenticado.getUsername(), ofertaId));
        model.addAttribute(
                "alunos",
                professorResponsavelService.listarAlunosDaOferta(usuarioAutenticado.getUsername(), ofertaId));
        model.addAttribute(
                "podeEncerrar",
                professorResponsavelService.podeEncerrarOferta(usuarioAutenticado.getUsername(), ofertaId));
        return "professor/detalhes-oferta";
    }

    @GetMapping("/{ofertaId}/matriculas/{matriculaId}")
    public String detalharMatricula(
            @PathVariable UUID ofertaId,
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model) {
        var matricula = professorResponsavelService.buscarDetalhesMatricula(usuarioAutenticado.getUsername(), matriculaId);
        var oferta = matricula.getOferta();
        model.addAttribute("oferta", oferta);
        model.addAttribute("ofertaResumo", professorResponsavelService.buscarResumoOferta(usuarioAutenticado.getUsername(), ofertaId));
        model.addAttribute("matricula", matricula);
        model.addAttribute("aluno", matricula.getAluno());
        model.addAttribute("plano", matricula.getPlanoTrabalho());
        model.addAttribute("documentacao", matricula.getDocumentacaoDocencia());
        model.addAttribute("relatorio", matricula.getRelatorioFinal());
        model.addAttribute("logs", professorResponsavelService.listarHistorico(matriculaId));
        return "professor/detalhes-aluno";
    }

    @GetMapping("/matriculas/{matriculaId}/concluir-relatorio")
    public String exibirConclusaoRelatorio(
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            var matricula = professorResponsavelService.buscarParaConcluirRelatorio(
                    usuarioAutenticado.getUsername(),
                    matriculaId);
            if (!model.containsAttribute("conclusaoResponsavelForm")) {
                var form = new ConclusaoResponsavelForm();
                if (matricula.getRelatorioFinal().getAprovacaoSupervisor() != null) {
                    form.setFrequenciaFinal(matricula.getRelatorioFinal().getAprovacaoSupervisor().getFrequencia());
                    form.setNotaFinal(matricula.getRelatorioFinal().getAprovacaoSupervisor().getSugestaoNota());
                }
                model.addAttribute("conclusaoResponsavelForm", form);
            }
            carregarModeloConclusao(model, matricula);
            model.addAttribute("historico", professorResponsavelService.listarHistorico(matricula.getId()));
            return "professor/avaliar-relatorio";
        } catch (ValidacaoNegocioException ex) {
            redirectAttributes.addFlashAttribute("errorMessageKey", ex.getMessageKey());
            return "redirect:/responsavel/ofertas";
        }
    }

    @PostMapping("/matriculas/{matriculaId}/concluir-relatorio")
    public String concluirRelatorio(
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @Valid @ModelAttribute("conclusaoResponsavelForm") ConclusaoResponsavelForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        var matricula = professorResponsavelService.buscarParaConcluirRelatorio(
                usuarioAutenticado.getUsername(),
                matriculaId);
        if (bindingResult.hasErrors()) {
            carregarModeloConclusao(model, matricula);
            model.addAttribute("historico", professorResponsavelService.listarHistorico(matricula.getId()));
            return "professor/avaliar-relatorio";
        }
        professorResponsavelService.concluirRelatorio(
                usuarioAutenticado.getUsername(),
                matriculaId,
                form.getParecer(),
                form.getFrequenciaFinal(),
                form.getNotaFinal());
        redirectAttributes.addFlashAttribute("successMessageKey", "responsavel.sucesso.relatorio.concluido");
        return "redirect:/responsavel/ofertas/" + matricula.getOferta().getId();
    }

    @GetMapping("/matriculas/{matriculaId}/analisar-documentacao")
    public String exibirAnaliseDocumentacao(
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            var matricula = professorResponsavelService.buscarParaAnalisarDocumentacao(
                    usuarioAutenticado.getUsername(),
                    matriculaId);
            if (!model.containsAttribute("conclusaoResponsavelForm")) {
                model.addAttribute("conclusaoResponsavelForm", new ConclusaoResponsavelForm());
            }
            carregarModeloConclusao(model, matricula);
            return "professor/analisar-documentacao";
        } catch (ValidacaoNegocioException ex) {
            redirectAttributes.addFlashAttribute("errorMessageKey", ex.getMessageKey());
            return "redirect:/responsavel/ofertas";
        }
    }

    @PostMapping("/matriculas/{matriculaId}/analisar-documentacao")
    public String analisarDocumentacao(
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @Valid @ModelAttribute("conclusaoResponsavelForm") ConclusaoResponsavelForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        var matricula = professorResponsavelService.buscarParaAnalisarDocumentacao(
                usuarioAutenticado.getUsername(),
                matriculaId);
        if (bindingResult.hasErrors()) {
            carregarModeloConclusao(model, matricula);
            return "professor/analisar-documentacao";
        }
        professorResponsavelService.analisarDocumentacao(
                usuarioAutenticado.getUsername(),
                matriculaId,
                form.getParecer(),
                form.getFrequenciaFinal(),
                form.getNotaFinal());
        redirectAttributes.addFlashAttribute("successMessageKey", "responsavel.sucesso.documentacao.analisada");
        return "redirect:/responsavel/ofertas/" + matricula.getOferta().getId();
    }

    @GetMapping("/{ofertaId}/encerrar")
    public String exibirEncerramentoOferta(
            @PathVariable UUID ofertaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model,
            RedirectAttributes redirectAttributes) {
        var username = usuarioAutenticado.getUsername();
        if (!professorResponsavelService.podeEncerrarOferta(username, ofertaId)) {
            redirectAttributes.addFlashAttribute("errorMessageKey", "responsavel.error.oferta.alunos.pendentes");
            return "redirect:/responsavel/ofertas/" + ofertaId;
        }
        model.addAttribute("oferta", professorResponsavelService.buscarOfertaDoResponsavel(username, ofertaId));
        model.addAttribute("alunos", professorResponsavelService.listarAlunosDaOferta(username, ofertaId));
        var estatisticas = professorResponsavelService.calcularEstatisticas(username, ofertaId);
        model.addAttribute("estatisticas", estatisticas);
        model.addAttribute("mediaFrequencia", estatisticas.frequencias().getAverage());
        if (!model.containsAttribute("encerrarOfertaResponsavelForm")) {
            model.addAttribute("encerrarOfertaResponsavelForm", new EncerrarOfertaResponsavelForm());
        }
        return "professor/encerrar-oferta";
    }

    @PostMapping("/{ofertaId}/encerrar")
    public String encerrarOferta(
            @PathVariable UUID ofertaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @Valid @ModelAttribute("encerrarOfertaResponsavelForm") EncerrarOfertaResponsavelForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        var username = usuarioAutenticado.getUsername();
        if (bindingResult.hasErrors()) {
            model.addAttribute("oferta", professorResponsavelService.buscarOfertaDoResponsavel(username, ofertaId));
            model.addAttribute("alunos", professorResponsavelService.listarAlunosDaOferta(username, ofertaId));
            var estatisticas = professorResponsavelService.calcularEstatisticas(username, ofertaId);
            model.addAttribute("estatisticas", estatisticas);
            model.addAttribute("mediaFrequencia", estatisticas.frequencias().getAverage());
            return "professor/encerrar-oferta";
        }
        professorResponsavelService.encerrarOferta(
                username,
                ofertaId,
                form.getLicoesAprendidas());
        redirectAttributes.addFlashAttribute("successMessageKey", "responsavel.sucesso.oferta.encerrada");
        return "redirect:/responsavel/ofertas/" + ofertaId;
    }

    @GetMapping("/matriculas/{matriculaId}/plano")
    public ResponseEntity<Resource> visualizarPlano(
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        var matricula = professorResponsavelService.buscarParaConcluirRelatorio(
                usuarioAutenticado.getUsername(),
                matriculaId);
        return criarRespostaPdf(matricula.getPlanoTrabalho().getArquivoPlano(), "plano-trabalho.pdf");
    }

    @GetMapping("/matriculas/{matriculaId}/relatorio")
    public ResponseEntity<Resource> visualizarRelatorio(
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        var matricula = professorResponsavelService.buscarParaConcluirRelatorio(
                usuarioAutenticado.getUsername(),
                matriculaId);
        return criarRespostaPdf(matricula.getRelatorioFinal().getArquivoRelatorio(), "relatorio-final.pdf");
    }

    @GetMapping("/matriculas/{matriculaId}/documentacao")
    public ResponseEntity<Resource> visualizarDocumentacao(
            @PathVariable UUID matriculaId,
            @AuthenticationPrincipal UserDetails usuarioAutenticado) {
        var matricula = professorResponsavelService.buscarParaAnalisarDocumentacao(
                usuarioAutenticado.getUsername(),
                matriculaId);
        return criarRespostaPdf(
                matricula.getDocumentacaoDocencia().getArquivoDocumentacao(),
                "documentacao-docencia.pdf");
    }

    private void carregarModeloConclusao(Model model, AlunoOfertaModel matricula) {
        model.addAttribute("matricula", matricula);
        model.addAttribute("oferta", matricula.getOferta());
        model.addAttribute("aluno", matricula.getAluno());
        model.addAttribute("plano", matricula.getPlanoTrabalho());
        model.addAttribute("relatorio", matricula.getRelatorioFinal());
        model.addAttribute("documentacao", matricula.getDocumentacaoDocencia());
        model.addAttribute("notas", br.ufscar.dc.dsw.PESCD.models.Nota.values());
    }

    private ResponseEntity<Resource> criarRespostaPdf(String arquivo, String nomeArquivo) {
        var caminho = Paths.get(arquivo).normalize();
        var recurso = new FileSystemResource(caminho);
        if (!recurso.exists() || !recurso.isReadable()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nomeArquivo + "\"")
                .body(recurso);
    }
}

package br.ufscar.dc.dsw.PESCD.antes_conversao;

import br.ufscar.dc.dsw.PESCD.dtos.OfertaForm;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.services.ConfiguracaoSistemaService;
import br.ufscar.dc.dsw.PESCD.services.OfertaService;
import br.ufscar.dc.dsw.PESCD.services.UsuarioService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.UUID;

@Controller
@RequestMapping("/secretario/ofertas")
public class SecretarioOfertaController {

    private final OfertaService ofertaService;
    private final UsuarioService usuarioService;
    private final ConfiguracaoSistemaService configuracaoSistemaService;

    public SecretarioOfertaController(
            OfertaService ofertaService,
            UsuarioService usuarioService,
            ConfiguracaoSistemaService configuracaoSistemaService) {
        this.ofertaService = ofertaService;
        this.usuarioService = usuarioService;
        this.configuracaoSistemaService = configuracaoSistemaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("ofertas", ofertaService.listarParaAcompanhamento());
        return "secretario/ofertas/lista";
    }

    @GetMapping("/nova")
    public String formNova(Model model) {
        var form = new OfertaForm();
        form.setDataInicio(LocalDate.now());
        form.setDataFim(LocalDate.now().plusMonths(4));
        model.addAttribute("ofertaForm", form);
        model.addAttribute("professores", usuarioService.listarProfessoresParaOferta());
        return "secretario/ofertas/form";
    }

    @PostMapping("/nova")
    public String salvarNova(
            @ModelAttribute OfertaForm form,
            RedirectAttributes redirectAttributes) {
        if (form.getSemestre() == null || form.getSemestre().isBlank()) {
            throw new ValidacaoNegocioException("oferta.error.semestre.obrigatorio");
        }
        if (form.getProfessorResponsavelId() == null) {
            throw new ValidacaoNegocioException("oferta.error.professor.obrigatorio");
        }
        var secretario = usuarioService.obterLogado();
        var oferta = ofertaService.salvar(form.toRecord(), secretario);
        redirectAttributes.addFlashAttribute("sucesso", "oferta.sucesso.criada");
        return "redirect:/secretario/ofertas/" + oferta.getId();
    }

    @GetMapping("/{id}")
    public String detalhes(@PathVariable UUID id, Model model) {
        var oferta = ofertaService.buscarDetalhes(id);
        model.addAttribute("oferta", oferta);
        model.addAttribute("ofertaResumo", ofertaService.toListagemDto(oferta));
        model.addAttribute("alunos", oferta.getAlunos());
        return "secretario/ofertas/detalhes";
    }

    @GetMapping("/{id}/exportar-resultados")
    public ResponseEntity<byte[]> exportarResultados(@PathVariable UUID id) {
        var resultado = ofertaService.exportarResultadosCsv(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resultado.nomeArquivo() + "\"")
                .body(resultado.conteudo());
    }

    @GetMapping("/{id}/encerrar")
    public String formEncerrar(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        var oferta = ofertaService.buscarPorId(id);
        if (!ofertaService.podeEncerrar(oferta)) {
            redirectAttributes.addFlashAttribute("erro", "oferta.error.encerrar.status.invalido");
            return "redirect:/secretario/ofertas/" + id;
        }
        model.addAttribute("oferta", oferta);
        model.addAttribute("instrucoes", configuracaoSistemaService.obterInstrucoesEncerramento());
        return "secretario/ofertas/encerrar";
    }

    @PostMapping("/{id}/encerrar")
    public String confirmarEncerrar(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        var secretario = usuarioService.obterLogado();
        ofertaService.encerrar(id, secretario);
        redirectAttributes.addFlashAttribute("sucesso", "oferta.sucesso.encerrada");
        return "redirect:/secretario/ofertas/" + id;
    }
}

package br.ufscar.dc.dsw.PESCD.controllers;

import br.ufscar.dc.dsw.PESCD.dtos.AlunoMatriculaForm;
import br.ufscar.dc.dsw.PESCD.exception.OfertaEncerradaException;
import br.ufscar.dc.dsw.PESCD.models.StatusOferta;
import br.ufscar.dc.dsw.PESCD.services.AlunoOfertaService;
import br.ufscar.dc.dsw.PESCD.services.LogStatusAlunoOfertaService;
import br.ufscar.dc.dsw.PESCD.services.OfertaService;
import br.ufscar.dc.dsw.PESCD.services.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequestMapping("/secretario/ofertas/{ofertaId}/alunos")
public class SecretarioAlunoOfertaController {

    private final OfertaService ofertaService;
    private final AlunoOfertaService alunoOfertaService;
    private final UsuarioService usuarioService;
    private final LogStatusAlunoOfertaService logStatusAlunoOfertaService;

    public SecretarioAlunoOfertaController(
            OfertaService ofertaService,
            AlunoOfertaService alunoOfertaService,
            UsuarioService usuarioService,
            LogStatusAlunoOfertaService logStatusAlunoOfertaService) {
        this.ofertaService = ofertaService;
        this.alunoOfertaService = alunoOfertaService;
        this.usuarioService = usuarioService;
        this.logStatusAlunoOfertaService = logStatusAlunoOfertaService;
    }

    @GetMapping
    public String listar(@PathVariable UUID ofertaId, Model model) {
        var oferta = ofertaService.buscarPorId(ofertaId);
        model.addAttribute("oferta", oferta);
        model.addAttribute("ofertaResumo", ofertaService.toListagemDto(oferta));
        model.addAttribute("alunos", alunoOfertaService.listarPorOferta(ofertaId));
        model.addAttribute("somenteLeitura", oferta.getStatus() == StatusOferta.CONCLUIDA);
        return "secretario/alunos/lista";
    }

    @GetMapping("/novo")
    public String formNovo(@PathVariable UUID ofertaId, Model model, RedirectAttributes redirectAttributes) {
        try {
            ofertaService.assertOfertaEditavel(ofertaService.buscarPorId(ofertaId));
        } catch (OfertaEncerradaException ex) {
            redirectAttributes.addFlashAttribute("erro", OfertaEncerradaException.MESSAGE_KEY);
            return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
        }
        model.addAttribute("oferta", ofertaService.buscarPorId(ofertaId));
        model.addAttribute("alunoForm", new AlunoMatriculaForm());
        return "secretario/alunos/form";
    }

    @PostMapping("/novo")
    public String salvarNovo(
            @PathVariable UUID ofertaId,
            @ModelAttribute AlunoMatriculaForm form,
            RedirectAttributes redirectAttributes) {
        var operador = usuarioService.obterLogado();
        alunoOfertaService.associar(ofertaId, form.toRecord(), operador);
        redirectAttributes.addFlashAttribute("sucesso", "aluno.oferta.sucesso.associado");
        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }

    @GetMapping("/importar-csv")
    public String formImportar(@PathVariable UUID ofertaId, Model model, RedirectAttributes redirectAttributes) {
        try {
            ofertaService.assertOfertaEditavel(ofertaService.buscarPorId(ofertaId));
        } catch (OfertaEncerradaException ex) {
            redirectAttributes.addFlashAttribute("erro", OfertaEncerradaException.MESSAGE_KEY);
            return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
        }
        model.addAttribute("oferta", ofertaService.buscarPorId(ofertaId));
        return "secretario/alunos/importar-csv";
    }

    @PostMapping("/importar-csv")
    public String importarCsv(
            @PathVariable UUID ofertaId,
            @RequestParam("arquivo") MultipartFile arquivo,
            RedirectAttributes redirectAttributes) {
        var operador = usuarioService.obterLogado();
        var resultado = alunoOfertaService.importarCsv(ofertaId, arquivo, operador);
        redirectAttributes.addFlashAttribute("sucesso", "csv.sucesso.importacao");
        redirectAttributes.addFlashAttribute("csvCriados", resultado.alunosCriados());
        redirectAttributes.addFlashAttribute("csvAssociados", resultado.alunosAssociados());
        redirectAttributes.addFlashAttribute("csvErros", resultado.linhasComErro());
        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }

    @GetMapping("/{matriculaId}/editar")
    public String formEditar(
            @PathVariable UUID ofertaId,
            @PathVariable UUID matriculaId,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            ofertaService.assertOfertaEditavel(ofertaService.buscarPorId(ofertaId));
        } catch (OfertaEncerradaException ex) {
            redirectAttributes.addFlashAttribute("erro", OfertaEncerradaException.MESSAGE_KEY);
            return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
        }
        var matricula = alunoOfertaService.buscarMatricula(ofertaId, matriculaId);
        model.addAttribute("oferta", ofertaService.buscarPorId(ofertaId));
        model.addAttribute("matricula", matricula);
        var alunoForm = new AlunoMatriculaForm();
        alunoForm.setRa(matricula.getAluno().getRa());
        alunoForm.setNomeCompleto(matricula.getAluno().getNomeCompleto());
        alunoForm.setEmail(matricula.getAluno().getEmail());
        model.addAttribute("alunoForm", alunoForm);
        return "secretario/alunos/form";
    }

    @PostMapping("/{matriculaId}/editar")
    public String salvarEditar(
            @PathVariable UUID ofertaId,
            @PathVariable UUID matriculaId,
            @ModelAttribute AlunoMatriculaForm form,
            RedirectAttributes redirectAttributes) {
        alunoOfertaService.atualizar(ofertaId, matriculaId, form.toRecord());
        redirectAttributes.addFlashAttribute("sucesso", "aluno.oferta.sucesso.atualizado");
        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }

    @PostMapping("/{matriculaId}/excluir")
    public String excluir(
            @PathVariable UUID ofertaId,
            @PathVariable UUID matriculaId,
            RedirectAttributes redirectAttributes) {
        alunoOfertaService.desassociar(ofertaId, matriculaId);
        redirectAttributes.addFlashAttribute("sucesso", "aluno.oferta.sucesso.removido");
        return "redirect:/secretario/ofertas/" + ofertaId + "/alunos";
    }

    @GetMapping("/{matriculaId}/detalhes")
    public String detalhes(
            @PathVariable UUID ofertaId,
            @PathVariable UUID matriculaId,
            Model model) {
        var matricula = alunoOfertaService.buscarMatricula(ofertaId, matriculaId);
        model.addAttribute("oferta", ofertaService.buscarPorId(ofertaId));
        model.addAttribute("matricula", matricula);
        model.addAttribute("logs", logStatusAlunoOfertaService.listarPorMatricula(matriculaId));
        return "secretario/alunos/detalhes";
    }
}

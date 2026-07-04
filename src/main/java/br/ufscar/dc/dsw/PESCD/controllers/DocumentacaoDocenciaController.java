package br.ufscar.dc.dsw.PESCD.controllers;

import br.ufscar.dc.dsw.PESCD.dtos.DocumentacaoDocenciaForm;
import br.ufscar.dc.dsw.PESCD.exception.ValidacaoNegocioException;
import br.ufscar.dc.dsw.PESCD.models.AlunoOfertaModel;
import br.ufscar.dc.dsw.PESCD.models.OfertaModel;
import br.ufscar.dc.dsw.PESCD.services.DocumentacaoDocenciaService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/aluno/oferta")
public class DocumentacaoDocenciaController {

    private final DocumentacaoDocenciaService documentacaoDocenciaService;

    public DocumentacaoDocenciaController(DocumentacaoDocenciaService documentacaoDocenciaService) {
        this.documentacaoDocenciaService = documentacaoDocenciaService;
    }

    @GetMapping("/{id}/enviar-documentacao")
    public String exibirFormulario(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            Model model,
            RedirectAttributes redirectAttributes) {

        AlunoOfertaModel matricula;
        try {
            matricula = documentacaoDocenciaService.buscarMatriculaElegivel(usuarioAutenticado.getUsername(), id);
        } catch (ValidacaoNegocioException ex) {
            redirectAttributes.addFlashAttribute("errorMessageKey", ex.getMessageKey());
            return "redirect:/aluno/ofertas";
        }
        if (!model.containsAttribute("documentacaoDocenciaForm")) {
            model.addAttribute("documentacaoDocenciaForm", new DocumentacaoDocenciaForm());
        }
        carregarModeloFormulario(model, matricula.getOferta());
        return "aluno/enviar-documentacao";
    }

    @PostMapping("/{id}/enviar-documentacao")
    public String enviarDocumentacao(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails usuarioAutenticado,
            @Valid @ModelAttribute("documentacaoDocenciaForm") DocumentacaoDocenciaForm form,
            BindingResult bindingResult,
            @RequestParam("arquivoDocumentacao") MultipartFile arquivoDocumentacao,
            Model model,
            RedirectAttributes redirectAttributes) {

        var matricula = documentacaoDocenciaService.buscarMatriculaElegivel(usuarioAutenticado.getUsername(), id);

        if (bindingResult.hasErrors()) {
            carregarModeloFormulario(model, matricula.getOferta());
            return "aluno/enviar-documentacao";
        }

        try {
            documentacaoDocenciaService.enviarDocumentacao(
                    usuarioAutenticado.getUsername(),
                    id,
                    form,
                    arquivoDocumentacao);
        } catch (ValidacaoNegocioException ex) {
            model.addAttribute("errorMessageKey", ex.getMessageKey());
            carregarModeloFormulario(model, matricula.getOferta());
            return "aluno/enviar-documentacao";
        }

        redirectAttributes.addFlashAttribute("successMessageKey", "documentacao.sucesso.enviada");
        return "redirect:/aluno/ofertas";
    }

    private void carregarModeloFormulario(Model model, OfertaModel oferta) {
        model.addAttribute("oferta", oferta);
    }
}

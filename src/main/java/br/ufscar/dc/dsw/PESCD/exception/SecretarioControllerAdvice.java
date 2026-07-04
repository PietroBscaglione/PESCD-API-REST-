package br.ufscar.dc.dsw.PESCD.exception;

import br.ufscar.dc.dsw.PESCD.controllers.SecretarioAlunoOfertaController;
import br.ufscar.dc.dsw.PESCD.controllers.SecretarioOfertaController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(assignableTypes = {SecretarioOfertaController.class, SecretarioAlunoOfertaController.class})
public class SecretarioControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(SecretarioControllerAdvice.class);

    @ExceptionHandler(ValidacaoNegocioException.class)
    public String handleValidacao(
            ValidacaoNegocioException ex,
            RedirectAttributes redirectAttributes,
            jakarta.servlet.http.HttpServletRequest request) {
        logger.warn("Validacao de negocio: {}", ex.getMessageKey());
        redirectAttributes.addFlashAttribute("erro", ex.getMessageKey());
        var referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/secretario/")) {
            return "redirect:" + referer.substring(referer.indexOf("/secretario/"));
        }
        return "redirect:/secretario/ofertas";
    }

    @ExceptionHandler(OfertaEncerradaException.class)
    public String handleOfertaEncerrada(OfertaEncerradaException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Oferta encerrada: operacao de escrita bloqueada");
        redirectAttributes.addFlashAttribute("erro", OfertaEncerradaException.MESSAGE_KEY);
        return "redirect:/secretario/ofertas";
    }

    @ExceptionHandler(CsvInvalidoException.class)
    public String handleCsv(
            CsvInvalidoException ex,
            RedirectAttributes redirectAttributes,
            jakarta.servlet.http.HttpServletRequest request) {
        logger.error("CSV invalido: {}", ex.getMessageKey());
        redirectAttributes.addFlashAttribute("erro", ex.getMessageKey());
        var referer = request.getHeader("Referer");
        if (referer != null && referer.contains("/alunos/importar-csv")) {
            return "redirect:" + referer.substring(referer.indexOf("/secretario/"));
        }
        return "redirect:/secretario/ofertas";
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public String handleNaoEncontrado(RecursoNaoEncontradoException ex, RedirectAttributes redirectAttributes) {
        logger.warn("Recurso nao encontrado");
        redirectAttributes.addFlashAttribute("erro", RecursoNaoEncontradoException.MESSAGE_KEY);
        return "redirect:/secretario/ofertas";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenerico(Exception ex, RedirectAttributes redirectAttributes) {
        logger.error("Erro inesperado no modulo secretario", ex);
        redirectAttributes.addFlashAttribute("erro", "error.generico");
        return "redirect:/secretario/ofertas";
    }
}

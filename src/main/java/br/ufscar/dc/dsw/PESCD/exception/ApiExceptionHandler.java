package br.ufscar.dc.dsw.PESCD.exception;

import br.ufscar.dc.dsw.PESCD.controllers.api.AuthApiController;
import br.ufscar.dc.dsw.PESCD.controllers.api.SecretarioApiController;
import br.ufscar.dc.dsw.PESCD.controllers.api.ProfessorSupervisorRestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice(assignableTypes = {
        AuthApiController.class,
        SecretarioApiController.class,
        ProfessorSupervisorRestController.class
})
public class ApiExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        var fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "invalid",
                        (first, ignored) -> first));
        return ResponseEntity.badRequest().body(new ApiErrorResponse(
                "VALIDATION_ERROR",
                "error.validacao",
                fieldErrors));
    }

    @ExceptionHandler(ValidacaoNegocioException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessValidation(ValidacaoNegocioException ex) {
        logger.warn("Validacao de negocio na API: {}", ex.getMessageKey());
        return ResponseEntity.badRequest().body(new ApiErrorResponse(
                "BUSINESS_VALIDATION",
                ex.getMessageKey(),
                Map.of()));
    }

    @ExceptionHandler(OfertaEncerradaException.class)
    public ResponseEntity<ApiErrorResponse> handleClosedOffering(OfertaEncerradaException ex) {
        logger.warn("Oferta encerrada: operacao REST de escrita bloqueada");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ApiErrorResponse(
                "OFFERING_CLOSED",
                OfertaEncerradaException.MESSAGE_KEY,
                Map.of()));
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(RecursoNaoEncontradoException ex) {
        logger.warn("Recurso nao encontrado na API");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiErrorResponse(
                "NOT_FOUND",
                RecursoNaoEncontradoException.MESSAGE_KEY,
                Map.of()));
    }

    @ExceptionHandler(CsvInvalidoException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCsv(CsvInvalidoException ex) {
        logger.warn("CSV invalido na API: {}", ex.getMessageKey());
        return ResponseEntity.badRequest().body(new ApiErrorResponse(
                "INVALID_CSV",
                ex.getMessageKey(),
                Map.of()));
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class
    })
    public ResponseEntity<ApiErrorResponse> handleMissingRequestPart(Exception ex) {
        logger.warn("Parametro obrigatorio ausente na API: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(new ApiErrorResponse(
                "MISSING_REQUEST_PART",
                "error.requisicao.invalida",
                Map.of()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        logger.error("Erro inesperado na API", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiErrorResponse(
                "INTERNAL_ERROR",
                "error.generico",
                Map.of()));
    }

    public record ApiErrorResponse(
            String error,
            String messageKey,
            Map<String, String> fieldErrors
    ) {
    }
}

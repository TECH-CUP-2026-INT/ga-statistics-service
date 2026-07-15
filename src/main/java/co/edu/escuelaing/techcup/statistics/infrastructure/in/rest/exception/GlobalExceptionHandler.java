package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.exception;

import co.edu.escuelaing.techcup.statistics.domain.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.domain.exception.ExternalServiceException;
import co.edu.escuelaing.techcup.statistics.domain.exception.RecognitionNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Traduce las excepciones de dominio (y de validacion web) a respuestas
 * HTTP. Es el unico lugar que conoce codigos de estado -- el dominio no
 * sabe que es un 409 o un 502, solo lanza una excepcion con significado de
 * negocio.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DuplicateMatchStatException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateMatchStatException ex,
                                                          HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(RecognitionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRecognitionNotFound(RecognitionNotFoundException ex,
                                                                    HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<ErrorResponse> handleExternalService(ExternalServiceException ex,
                                                                HttpServletRequest request) {
        return build(HttpStatus.BAD_GATEWAY, List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                           HttpServletRequest request) {
        List<String> messages = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        return build(HttpStatus.BAD_REQUEST, messages, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex,
                                                                HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, List.of(ex.getMessage()), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("Ocurrió un error inesperado: " + ex.getMessage()), request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, List<String> messages,
                                                 HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                messages,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}

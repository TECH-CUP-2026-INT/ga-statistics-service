package co.edu.escuelaing.techcup.statistics.infrastructure.exception;

import co.edu.escuelaing.techcup.statistics.domain.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.domain.exception.RecognitionNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

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
                .map(fe -> "El campo '" + fe.getField() + "' " + fe.getDefaultMessage())
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
                List.of("Ocurrió un error inesperado en el servidor: " + ex.getMessage()), request);
    }

    private static String reasonInSpanish(HttpStatus status) {
        return switch (status) {
            case BAD_REQUEST -> "Solicitud inválida";
            case NOT_FOUND -> "No encontrado";
            case CONFLICT -> "Conflicto";
            case BAD_GATEWAY -> "Servicio externo no disponible";
            case INTERNAL_SERVER_ERROR -> "Error interno del servidor";
            default -> status.getReasonPhrase();
        };
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, List<String> messages,
                                                 HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(ZoneId.systemDefault()), status.value(), reasonInSpanish(status), messages, request.getRequestURI());
        return ResponseEntity.status(status).body(body);
    }
}

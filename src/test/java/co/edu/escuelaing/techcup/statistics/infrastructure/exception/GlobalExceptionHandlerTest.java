package co.edu.escuelaing.techcup.statistics.infrastructure.exception;

import co.edu.escuelaing.techcup.statistics.domain.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.domain.exception.RecognitionNotFoundException;
import java.util.UUID;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/test");
    }

    @Test
    void handleDuplicateMatchStat_deberiaRetornar409() {
        var ex = new DuplicateMatchStatException(UUID.randomUUID(), UUID.randomUUID());
        ResponseEntity<ErrorResponse> response = handler.handleDuplicate(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().messages()).first().asString().contains("Ya existe");
    }

    @Test
    void handleRecognitionNotFound_deberiaRetornar404() {
        var ex = new RecognitionNotFoundException(UUID.randomUUID());
        ResponseEntity<ErrorResponse> response = handler.handleRecognitionNotFound(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().messages()).first().asString().contains("no se ha generado");
    }

    @Test
    void handleExternalService_deberiaRetornar502() {
        var ex = new ExternalServiceException("error externo");
        ResponseEntity<ErrorResponse> response = handler.handleExternalService(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_GATEWAY);
        assertThat(response.getBody().messages()).first().asString().contains("error externo");
    }

    @Test
    void handleIllegalArgument_deberiaRetornar400() {
        var ex = new IllegalArgumentException("dato invalido");
        ResponseEntity<ErrorResponse> response = handler.handleIllegalArgument(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void handleGeneric_deberiaRetornar500() {
        var ex = new RuntimeException("error inesperado");
        ResponseEntity<ErrorResponse> response = handler.handleGeneric(ex, request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

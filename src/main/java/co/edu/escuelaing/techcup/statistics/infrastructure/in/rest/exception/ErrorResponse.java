package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        List<String> messages,
        String path
) {
}

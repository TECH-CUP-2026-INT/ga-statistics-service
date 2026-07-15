package co.edu.escuelaing.techcup.statistics.domain.exception;

/** Regla de negocio: una dependencia externa (ej. Torneos) no respondio correctamente. */
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }
}

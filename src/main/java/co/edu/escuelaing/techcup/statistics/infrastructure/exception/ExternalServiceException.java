package co.edu.escuelaing.techcup.statistics.infrastructure.exception;

/** Excepción lanzada cuando un servicio externo falla o no está disponible. */
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }
}

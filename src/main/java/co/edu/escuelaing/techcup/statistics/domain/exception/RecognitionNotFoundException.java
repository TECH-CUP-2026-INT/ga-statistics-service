package co.edu.escuelaing.techcup.statistics.domain.exception;

/** Regla de negocio: no se puede consultar un reconocimiento que no se ha generado. */
public class RecognitionNotFoundException extends RuntimeException {
    public RecognitionNotFoundException(String tournamentId) {
        super("Aún no se ha generado el reconocimiento para el torneo " + tournamentId
                + ". Debe finalizarse el torneo primero.");
    }
}

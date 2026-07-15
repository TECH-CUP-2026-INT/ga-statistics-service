package co.edu.escuelaing.techcup.statistics.domain.exception;

/**
 * Excepción de dominio: no se ha generado el reconocimiento para el torneo
 * solicitado.
 */
public class RecognitionNotFoundException extends RuntimeException {

    public RecognitionNotFoundException(String tournamentId) {
        super("Aún no se ha generado el reconocimiento para el torneo " + tournamentId
                + ". Debe finalizarse el torneo primero para que los reconocimientos sean calculados.");
    }
}

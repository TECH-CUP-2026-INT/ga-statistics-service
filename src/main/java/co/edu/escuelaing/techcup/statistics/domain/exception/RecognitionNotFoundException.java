package co.edu.escuelaing.techcup.statistics.domain.exception;
import java.util.UUID;
/**
 * ExcepciÃ³n de dominio: no se ha generado el reconocimiento para el torneo
 * solicitado.
 */
public class RecognitionNotFoundException extends RuntimeException {

    public RecognitionNotFoundException(UUID tournamentId) {
        super("AÃºn no se ha generado el reconocimiento para el torneo " + tournamentId
                + ". Debe finalizarse el torneo primero para que los reconocimientos sean calculados.");
    }
}

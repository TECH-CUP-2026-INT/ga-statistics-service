package co.edu.escuelaing.techcup.statistics.exception;

public class RecognitionNotFoundException extends RuntimeException {
    public RecognitionNotFoundException(String tournamentId) {
        super("Aún no se ha generado el reconocimiento para el torneo " + tournamentId
                + ". Debe finalizarse el torneo primero.");
    }
}

package co.edu.escuelaing.techcup.statistics.exception;

public class DuplicateMatchStatException extends RuntimeException {
    public DuplicateMatchStatException(String playerId, String matchId) {
        super("Ya existe una estadística registrada para el jugador " + playerId
                + " en el partido " + matchId);
    }
}

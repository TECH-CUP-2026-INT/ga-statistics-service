package co.edu.escuelaing.techcup.statistics.exception;

public class DuplicateMatchStatException extends RuntimeException {
    public DuplicateMatchStatException(Long playerId, Long matchId) {
        super("Ya existe una estadística registrada para el jugador " + playerId
                + " en el partido " + matchId);
    }
}

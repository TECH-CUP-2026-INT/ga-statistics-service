package co.edu.escuelaing.techcup.statistics.domain.exception;

/** Regla de negocio: un jugador no puede tener dos registros del mismo partido. */
public class DuplicateMatchStatException extends RuntimeException {
    public DuplicateMatchStatException(String playerId, String matchId) {
        super("Ya existe una estadística registrada para el jugador " + playerId
                + " en el partido " + matchId);
    }
}

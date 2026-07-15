package co.edu.escuelaing.techcup.statistics.domain.exception;

/**
 * Excepción de dominio: ya existe una estadística registrada para el
 * mismo jugador en el mismo partido.
 */
public class DuplicateMatchStatException extends RuntimeException {

    public DuplicateMatchStatException(String playerId, String matchId) {
        super("Ya existe una estadística registrada para el jugador " + playerId
                + " en el partido " + matchId
                + ". No se puede registrar un evento duplicado para el mismo jugador y partido.");
    }
}

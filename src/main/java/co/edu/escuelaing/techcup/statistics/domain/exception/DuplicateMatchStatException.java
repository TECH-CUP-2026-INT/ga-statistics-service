package co.edu.escuelaing.techcup.statistics.domain.exception;
import java.util.UUID;
/**
 * ExcepciÃ³n de dominio: ya existe una estadÃ­stica registrada para el
 * mismo jugador en el mismo partido.
 */
public class DuplicateMatchStatException extends RuntimeException {

    public DuplicateMatchStatException(UUID playerId, UUID matchId) {
        super("Ya existe una estadÃ­stica registrada para el jugador " + playerId
                + " en el partido " + matchId
                + ". No se puede registrar un evento duplicado para el mismo jugador y partido.");
    }
}

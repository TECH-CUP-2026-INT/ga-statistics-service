package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;
import java.util.UUID;
import co.edu.escuelaing.techcup.statistics.domain.model.MatchResult;

import java.time.LocalDateTime;

/**
 * Evento de estadÃ­sticas de partido.
 * <p>
 * Publicado por el servicio de Competencia cuando finaliza un partido.
 * Contiene el resumen de UN jugador en UN partido.
 * Routing key: {@code techcup.match.event.finished}
 */
public record MatchStatEvent(
        UUID playerId,
        UUID teamId,
        UUID matchId,
        UUID tournamentId,
        MatchResult result,
        Integer goals,
        Integer yellowCards,
        Integer redCards,
        Integer foulsCommitted,
        Integer minutesPlayed,
        Integer assists,
        Boolean goalkeeper,
        LocalDateTime occurredAt
) {}

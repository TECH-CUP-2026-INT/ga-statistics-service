package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * Evento de inicio de partido.
 * Routing key: {@code techcup.match.event.started}
 */
public record MatchStartedEvent(
        UUID matchId,
        UUID tournamentId,
        String teamHomeId,
        String teamAwayId,
        LocalDateTime startedAt
) {}

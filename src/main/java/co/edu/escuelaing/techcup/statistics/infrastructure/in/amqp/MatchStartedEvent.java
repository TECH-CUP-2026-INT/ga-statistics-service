package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;

import java.time.LocalDateTime;

/**
 * Evento de inicio de partido.
 * Routing key: {@code techcup.match.event.started}
 */
public record MatchStartedEvent(
        String matchId,
        String tournamentId,
        String teamHomeId,
        String teamAwayId,
        LocalDateTime startedAt
) {}

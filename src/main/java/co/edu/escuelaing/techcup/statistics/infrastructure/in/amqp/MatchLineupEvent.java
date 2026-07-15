package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;

import java.util.List;

/**
 * Evento de alineación de partido.
 * Routing key: {@code techcup.match.event.lineup}
 */
public record MatchLineupEvent(
        String matchId,
        String tournamentId,
        String teamHomeId,
        String teamAwayId,
        List<String> homePlayerIds,
        List<String> awayPlayerIds,
        List<String> goalkeeperIds
) {}

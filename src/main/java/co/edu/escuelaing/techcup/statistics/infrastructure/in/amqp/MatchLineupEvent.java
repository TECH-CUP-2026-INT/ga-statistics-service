package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;
import java.util.UUID;
import java.util.List;

/**
 * Evento de alineaciÃ³n de partido.
 * Routing key: {@code techcup.match.event.lineup}
 */
public record MatchLineupEvent(
        UUID matchId,
        UUID tournamentId,
        String teamHomeId,
        String teamAwayId,
        List<String> homePlayerIds,
        List<String> awayPlayerIds,
        List<String> goalkeeperIds
) {}

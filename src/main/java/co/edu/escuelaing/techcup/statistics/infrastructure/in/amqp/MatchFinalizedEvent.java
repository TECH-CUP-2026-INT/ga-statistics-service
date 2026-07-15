package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;

import java.time.LocalDateTime;

/**
 * Evento de finalización de partido.
 * Indica que el árbitro finalizó el partido oficialmente.
 * routing key: {@code techcup.match.event.finalized}
 */
public record MatchFinalizedEvent(
        String matchId,
        String tournamentId,
        String teamHomeId,
        String teamAwayId,
        int homeScore,
        int awayScore,
        String result,
        LocalDateTime finalizedAt
) {
    public static final String RESULT_HOME_WIN = "HOME_WIN";
    public static final String RESULT_AWAY_WIN = "AWAY_WIN";
    public static final String RESULT_DRAW = "DRAW";
}

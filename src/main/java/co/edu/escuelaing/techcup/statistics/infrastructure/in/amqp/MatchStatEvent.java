package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;

import co.edu.escuelaing.techcup.statistics.domain.model.MatchResult;

import java.time.LocalDateTime;

/**
 * Evento de estadísticas de partido.
 * <p>
 * Publicado por el servicio de Competencia cuando finaliza un partido.
 * Contiene el resumen de UN jugador en UN partido.
 * Routing key: {@code techcup.match.event.finished}
 */
public record MatchStatEvent(
        String playerId,
        String teamId,
        String matchId,
        String tournamentId,
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

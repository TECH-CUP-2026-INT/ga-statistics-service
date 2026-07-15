package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;

import java.time.LocalDateTime;

/**
 * Evento de finalización de torneo.
 * <p>
 * Publicado por el servicio de Torneos cuando un torneo finaliza.
 * Estadísticas consume este evento para generar los reconocimientos
 * (máximo goleador y malla menos vencida).
 * Routing key: {@code techcup.tournament.event.finalized}
 */
public record TournamentFinalizedEvent(
        String tournamentId,
        LocalDateTime occurredAt
) {}

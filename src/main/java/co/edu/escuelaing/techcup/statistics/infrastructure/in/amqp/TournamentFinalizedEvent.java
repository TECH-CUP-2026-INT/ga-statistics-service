package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * Evento de finalizaciÃ³n de torneo.
 * <p>
 * Publicado por el servicio de Torneos cuando un torneo finaliza.
 * EstadÃ­sticas consume este evento para generar los reconocimientos
 * (mÃ¡ximo goleador y malla menos vencida).
 * Routing key: {@code techcup.tournament.event.finalized}
 */
public record TournamentFinalizedEvent(
        UUID tournamentId,
        LocalDateTime occurredAt
) {}

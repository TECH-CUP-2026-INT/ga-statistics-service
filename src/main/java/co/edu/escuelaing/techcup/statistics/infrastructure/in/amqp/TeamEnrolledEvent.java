package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;
import java.util.UUID;
import java.time.LocalDateTime;

/**
 * Evento de inscripciÃ³n de equipo en torneo.
 * Routing key: {@code techcup.tournament.team.enrolled}
 */
public record TeamEnrolledEvent(
        UUID tournamentId,
        UUID teamId,
        String teamName,
        LocalDateTime enrolledAt
) {}

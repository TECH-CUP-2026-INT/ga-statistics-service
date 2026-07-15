package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;

import java.time.LocalDateTime;

/**
 * Evento de inscripción de equipo en torneo.
 * Routing key: {@code techcup.tournament.team.enrolled}
 */
public record TeamEnrolledEvent(
        String tournamentId,
        String teamId,
        String teamName,
        LocalDateTime enrolledAt
) {}

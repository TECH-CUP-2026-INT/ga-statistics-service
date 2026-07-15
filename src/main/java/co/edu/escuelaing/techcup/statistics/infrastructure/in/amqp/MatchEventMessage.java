package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;

import co.edu.escuelaing.techcup.statistics.domain.model.MatchResult;

import java.time.LocalDateTime;

/**
 * DTO para eventos de partido recibidos por RabbitMQ.
 * <p>
 * Representa el payload que otros servicios (Competencia, Torneos)
 * envían al broker para que Estadísticas lo consuma de forma asíncrona.
 * <p>
 * Pendiente de definir con el equipo de Notificaciones la estructura
 * definitiva del mensaje.
 */
public record MatchEventMessage(
        String eventId,
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

package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.request;

import co.edu.escuelaing.techcup.statistics.domain.model.MatchResult;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MatchStatEventRequest(

        @NotBlank(message = "El identificador del jugador (playerId) es obligatorio")
        String playerId,

        @NotBlank(message = "El identificador del equipo (teamId) es obligatorio")
        String teamId,

        @NotBlank(message = "El identificador del partido (matchId) es obligatorio")
        String matchId,

        @NotBlank(message = "El identificador del torneo (tournamentId) es obligatorio")
        String tournamentId,

        @NotNull(message = "El resultado del partido (result) es obligatorio")
        MatchResult result,

        @Min(value = 0, message = "El número de goles no puede ser negativo")
        Integer goals,

        @Min(value = 0, message = "El número de tarjetas amarillas no puede ser negativo")
        Integer yellowCards,

        @Min(value = 0, message = "El número de tarjetas rojas no puede ser negativo")
        Integer redCards,

        @Min(value = 0, message = "El número de faltas cometidas no puede ser negativo")
        Integer foulsCommitted,

        @Min(value = 0, message = "Los minutos jugados no pueden ser negativos")
        Integer minutesPlayed,

        @Min(value = 0, message = "El número de asistencias no puede ser negativo")
        Integer assists,

        Boolean goalkeeper
) {}

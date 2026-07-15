package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.request;

import co.edu.escuelaing.techcup.statistics.domain.model.MatchResult;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Evento que el servicio de Competencia envía a Estadísticas cuando
 * finaliza un partido, con el resumen de UN jugador en ese partido.
 */
public record MatchStatEventRequest(

        @NotBlank(message = "playerId es obligatorio")
        String playerId,

        @NotBlank(message = "teamId es obligatorio")
        String teamId,

        @NotBlank(message = "matchId es obligatorio")
        String matchId,

        @NotBlank(message = "tournamentId es obligatorio")
        String tournamentId,

        @NotNull(message = "result es obligatorio")
        MatchResult result,

        @Min(value = 0, message = "goals no puede ser negativo")
        Integer goals,

        @Min(value = 0, message = "yellowCards no puede ser negativo")
        Integer yellowCards,

        @Min(value = 0, message = "redCards no puede ser negativo")
        Integer redCards,

        @Min(value = 0, message = "foulsCommitted no puede ser negativo")
        Integer foulsCommitted,

        @Min(value = 0, message = "minutesPlayed no puede ser negativo")
        Integer minutesPlayed,

        @Min(value = 0, message = "assists no puede ser negativo")
        Integer assists,

        Boolean goalkeeper
) {
}

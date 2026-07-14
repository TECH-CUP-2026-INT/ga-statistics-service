package co.edu.escuelaing.techcup.statistics.dto;

import co.edu.escuelaing.techcup.statistics.entity.MatchResult;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * Evento que el servicio de Competencia envía a Estadísticas cuando
 * finaliza un partido, con el resumen de UN jugador en ese partido.
 */
public record MatchStatEventRequest(

        @NotNull(message = "playerId es obligatorio")
        Long playerId,

        @NotNull(message = "teamId es obligatorio")
        Long teamId,

        @NotNull(message = "matchId es obligatorio")
        Long matchId,

        @NotNull(message = "tournamentId es obligatorio")
        Long tournamentId,

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

        /** true si este jugador jugó como portero en este partido. Opcional, default false. */
        Boolean goalkeeper
) {
}

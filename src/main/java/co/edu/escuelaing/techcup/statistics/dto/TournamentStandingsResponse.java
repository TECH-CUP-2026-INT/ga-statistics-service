package co.edu.escuelaing.techcup.statistics.dto;

import java.util.List;

/**
 * Estadísticas generales del torneo: la tabla de posiciones completa,
 * ordenada por puntos (y diferencia de gol como criterio de desempate).
 */
public record TournamentStandingsResponse(
        Long tournamentId,
        List<TeamStatisticsResponse> standings
) {
}

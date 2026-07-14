package co.edu.escuelaing.techcup.statistics.dto;

/**
 * Promedios calculados sobre TODOS los partidos finalizados de un torneo
 * (no por jugador ni por equipo). Ej: goles totales del torneo / cantidad
 * de partidos jugados.
 */
public record TournamentMatchAveragesResponse(
        Long tournamentId,
        long matchesConsidered,
        double averageGoalsPerMatch,
        double averageFoulsPerMatch,
        double averageCardsPerMatch
) {
}

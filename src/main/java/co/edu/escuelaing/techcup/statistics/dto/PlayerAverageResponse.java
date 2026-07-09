package co.edu.escuelaing.techcup.statistics.dto;

/**
 * Respuesta genérica para cualquier consulta de promedio de un jugador
 * (goles, faltas, minutos, % de partidos ganados).
 *
 * @param playerId          jugador consultado
 * @param tournamentId      torneo al que se filtró, o null si es histórico general
 * @param metric            nombre de la métrica (ej: "averageGoals")
 * @param value             valor calculado
 * @param matchesConsidered cantidad de partidos usados para el cálculo
 */
public record PlayerAverageResponse(
        Long playerId,
        Long tournamentId,
        String metric,
        double value,
        long matchesConsidered
) {
}

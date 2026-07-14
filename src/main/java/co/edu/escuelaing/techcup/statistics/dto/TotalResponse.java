package co.edu.escuelaing.techcup.statistics.dto;

/**
 * Respuesta genérica para consultas de TOTAL (a diferencia de
 * PlayerAverageResponse, que es para promedios). ownerId es el playerId o
 * teamId según el endpoint.
 */
public record TotalResponse(
        Long ownerId,
        Long tournamentId,
        String metric,
        long total,
        long matchesConsidered
) {
}

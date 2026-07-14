package co.edu.escuelaing.techcup.statistics.dto;

public record PlayerAverageResponse(
        String playerId,
        String tournamentId,
        String metric,
        double value,
        long matchesConsidered
) {
}

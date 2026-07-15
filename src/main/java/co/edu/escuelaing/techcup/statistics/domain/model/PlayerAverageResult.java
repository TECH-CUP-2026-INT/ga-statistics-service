package co.edu.escuelaing.techcup.statistics.domain.model;

public record PlayerAverageResult(
        String playerId,
        String tournamentId,
        String metric,
        double value,
        long matchesConsidered
) {
}

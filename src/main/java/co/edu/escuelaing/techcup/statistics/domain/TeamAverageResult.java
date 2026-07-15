package co.edu.escuelaing.techcup.statistics.domain;

public record TeamAverageResult(
        String teamId,
        String tournamentId,
        String metric,
        double value,
        long matchesConsidered
) {
}

package co.edu.escuelaing.techcup.statistics.dto;

public record TeamAverageResponse(
        String teamId,
        String tournamentId,
        String metric,
        double value,
        long matchesConsidered
) {
}

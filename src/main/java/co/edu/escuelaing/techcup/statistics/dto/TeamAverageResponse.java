package co.edu.escuelaing.techcup.statistics.dto;

public record TeamAverageResponse(
        Long teamId,
        Long tournamentId,
        String metric,
        double value,
        long matchesConsidered
) {
}

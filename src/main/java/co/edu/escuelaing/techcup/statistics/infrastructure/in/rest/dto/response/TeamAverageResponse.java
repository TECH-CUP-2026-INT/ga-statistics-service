package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

public record TeamAverageResponse(
        String teamId,
        String tournamentId,
        String metric,
        double value,
        long matchesConsidered
) {
}

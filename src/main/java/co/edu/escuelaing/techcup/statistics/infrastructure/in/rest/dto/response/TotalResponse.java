package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

public record TotalResponse(
        String ownerId,
        String tournamentId,
        String metric,
        long total,
        long matchesConsidered
) {
}

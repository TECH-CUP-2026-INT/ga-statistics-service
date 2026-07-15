package co.edu.escuelaing.techcup.statistics.domain;

public record TotalResult(
        String ownerId,
        String tournamentId,
        String metric,
        long total,
        long matchesConsidered
) {
}

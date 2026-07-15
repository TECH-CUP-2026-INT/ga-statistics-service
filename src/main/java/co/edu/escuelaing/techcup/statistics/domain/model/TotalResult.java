package co.edu.escuelaing.techcup.statistics.domain.model;

public record TotalResult(
        String entityId,
        String tournamentId,
        String metric,
        long total,
        long eventsConsidered
) {}

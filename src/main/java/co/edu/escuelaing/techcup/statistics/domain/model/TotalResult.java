package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
public record TotalResult(
        UUID entityId,
        UUID tournamentId,
        String metric,
        long total,
        long eventsConsidered
) {}

package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
public record TeamAverageResult(
        UUID teamId,
        UUID tournamentId,
        String metric,
        double value,
        long matchesConsidered
) {}

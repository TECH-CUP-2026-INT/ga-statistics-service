package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
public record PlayerAverageResult(
        UUID playerId,
        UUID tournamentId,
        String metric,
        double value,
        long matchesConsidered
) {}

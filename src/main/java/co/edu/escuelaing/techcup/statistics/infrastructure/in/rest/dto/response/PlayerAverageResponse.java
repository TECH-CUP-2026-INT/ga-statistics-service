package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
public record PlayerAverageResponse(
        UUID playerId, UUID tournamentId, String metric,
        double value, long matchesConsidered
) {}

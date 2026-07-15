package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

public record PlayerAverageResponse(
        String playerId, String tournamentId, String metric,
        double value, long matchesConsidered
) {}

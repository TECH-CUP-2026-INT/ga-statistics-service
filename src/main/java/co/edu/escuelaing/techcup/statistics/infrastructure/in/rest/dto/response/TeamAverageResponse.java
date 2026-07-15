package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
public record TeamAverageResponse(UUID teamId, UUID tournamentId, String metric, double value, long matchesConsidered) {}

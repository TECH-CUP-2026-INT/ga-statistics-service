package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
public record MatchesPlayedResponse(UUID playerId, UUID tournamentId, long matchesPlayed) {}

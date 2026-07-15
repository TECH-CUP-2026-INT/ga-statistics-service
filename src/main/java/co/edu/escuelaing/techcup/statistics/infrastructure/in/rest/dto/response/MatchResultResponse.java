package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
import java.util.List;

public record MatchResultResponse(UUID matchId, UUID tournamentId, List<TeamResult> teamResults) {
    public record TeamResult(UUID teamId, String result) {}
}

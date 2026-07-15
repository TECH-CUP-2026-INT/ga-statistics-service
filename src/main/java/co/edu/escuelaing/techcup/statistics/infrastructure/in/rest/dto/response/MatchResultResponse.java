package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

import java.util.List;

public record MatchResultResponse(String matchId, String tournamentId, List<TeamResult> teamResults) {
    public record TeamResult(String teamId, String result) {}
}

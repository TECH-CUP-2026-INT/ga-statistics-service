package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

import co.edu.escuelaing.techcup.statistics.domain.model.MatchResult;

import java.util.List;

public record MatchResultResponse(
        String matchId,
        String tournamentId,
        List<TeamResult> teamResults
) {
    public record TeamResult(String teamId, MatchResult result) {
    }
}

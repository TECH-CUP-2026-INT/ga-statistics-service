package co.edu.escuelaing.techcup.statistics.dto;

import co.edu.escuelaing.techcup.statistics.domain.MatchResult;

import java.util.List;

public record MatchResultResponse(
        String matchId,
        String tournamentId,
        List<TeamResult> teamResults
) {
    public record TeamResult(String teamId, MatchResult result) {
    }
}

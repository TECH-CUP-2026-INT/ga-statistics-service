package co.edu.escuelaing.techcup.statistics.dto;

import co.edu.escuelaing.techcup.statistics.entity.MatchResult;

import java.util.List;

public record MatchResultResponse(
        Long matchId,
        Long tournamentId,
        List<TeamResult> teamResults
) {
    public record TeamResult(Long teamId, MatchResult result) {
    }
}

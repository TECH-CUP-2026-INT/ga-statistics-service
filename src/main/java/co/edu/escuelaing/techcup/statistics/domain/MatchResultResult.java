package co.edu.escuelaing.techcup.statistics.domain;

import java.util.List;

public record MatchResultResult(
        String matchId,
        String tournamentId,
        List<TeamResultEntry> teamResults
) {
    public record TeamResultEntry(String teamId, MatchResult result) {
    }
}

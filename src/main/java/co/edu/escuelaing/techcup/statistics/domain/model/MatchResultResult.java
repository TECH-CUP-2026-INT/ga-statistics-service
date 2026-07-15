package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
import java.util.List;

public record MatchResultResult(
        UUID matchId,
        UUID tournamentId,
        List<TeamResultEntry> teamResults
) {
    public record TeamResultEntry(UUID teamId, MatchResult result) {}
}

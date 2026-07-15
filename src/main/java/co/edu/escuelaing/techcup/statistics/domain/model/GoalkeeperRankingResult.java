package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
import java.util.List;

public record GoalkeeperRankingResult(
        UUID tournamentId,
        List<GoalkeeperEntry> entries
) {
    public record GoalkeeperEntry(int position, UUID playerId, long goalsConceded) {}
}

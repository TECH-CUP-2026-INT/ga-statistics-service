package co.edu.escuelaing.techcup.statistics.domain;

import java.util.List;

public record GoalkeeperRankingResult(
        String tournamentId,
        List<GoalkeeperEntry> entries
) {
    public record GoalkeeperEntry(int position, String playerId, long goalsConceded) {
    }
}

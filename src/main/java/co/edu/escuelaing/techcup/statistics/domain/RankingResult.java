package co.edu.escuelaing.techcup.statistics.domain;

import java.util.List;

public record RankingResult(
        String type,
        String tournamentId,
        List<RankingEntry> entries
) {
    public record RankingEntry(int position, String playerId, long value) {
    }
}

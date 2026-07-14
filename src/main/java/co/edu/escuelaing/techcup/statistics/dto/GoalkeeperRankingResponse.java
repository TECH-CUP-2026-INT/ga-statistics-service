package co.edu.escuelaing.techcup.statistics.dto;

import java.util.List;

public record GoalkeeperRankingResponse(
        String tournamentId,
        List<Entry> entries
) {
    public record Entry(int position, String playerId, long goalsConceded) {
    }
}

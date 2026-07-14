package co.edu.escuelaing.techcup.statistics.dto;

import java.util.List;

public record GoalkeeperRankingResponse(
        Long tournamentId,
        List<Entry> entries
) {
    public record Entry(int position, Long playerId, long goalsConceded) {
    }
}

package co.edu.escuelaing.techcup.statistics.dto;

import java.util.List;

public record RankingResponse(
        String type,
        String tournamentId,
        List<RankingEntryResponse> entries
) {
}

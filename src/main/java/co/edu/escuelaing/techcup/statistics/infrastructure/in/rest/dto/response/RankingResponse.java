package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

import java.util.List;

public record RankingResponse(
        String type,
        String tournamentId,
        List<RankingEntryResponse> entries
) {
}

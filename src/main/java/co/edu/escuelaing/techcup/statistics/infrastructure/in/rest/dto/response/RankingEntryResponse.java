package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

public record RankingEntryResponse(
        int position,
        String playerId,
        long value
) {
}

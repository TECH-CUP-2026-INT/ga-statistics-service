package co.edu.escuelaing.techcup.statistics.dto;

public record RankingEntryResponse(
        int position,
        String playerId,
        long value
) {
}

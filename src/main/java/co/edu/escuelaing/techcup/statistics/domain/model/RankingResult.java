package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
import java.util.List;

public record RankingResult(
        String type,
        UUID tournamentId,
        List<RankingEntry> entries
) {
    public record RankingEntry(int position, UUID playerId, long value) {}
}

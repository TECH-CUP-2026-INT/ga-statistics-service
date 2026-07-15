package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
import java.util.List;

public record GoalkeeperRankingResponse(UUID tournamentId, List<Entry> entries) {
    public record Entry(int position, UUID playerId, long goalsConceded) {}
}

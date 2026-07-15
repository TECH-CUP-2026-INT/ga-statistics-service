package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
public record RankingEntryResponse(int position, UUID playerId, long value) {}

package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
import java.util.List;

public record RankingResponse(String type, UUID tournamentId, List<RankingEntryResponse> entries) {}

package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
public record PlayerCardsResponse(UUID playerId, UUID tournamentId, long yellowCards, long redCards) {}

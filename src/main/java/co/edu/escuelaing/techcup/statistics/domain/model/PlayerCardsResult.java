package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
public record PlayerCardsResult(
        UUID playerId,
        UUID tournamentId,
        long yellowCards,
        long redCards
) {}

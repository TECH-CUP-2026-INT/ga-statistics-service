package co.edu.escuelaing.techcup.statistics.domain.model;

public record PlayerCardsResult(
        String playerId,
        String tournamentId,
        long yellowCards,
        long redCards
) {
}

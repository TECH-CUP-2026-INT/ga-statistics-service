package co.edu.escuelaing.techcup.statistics.domain;

public record PlayerCardsResult(
        String playerId,
        String tournamentId,
        long yellowCards,
        long redCards
) {
}

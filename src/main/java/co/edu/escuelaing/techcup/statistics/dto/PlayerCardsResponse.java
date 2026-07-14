package co.edu.escuelaing.techcup.statistics.dto;

public record PlayerCardsResponse(
        String playerId,
        String tournamentId,
        long yellowCards,
        long redCards
) {
}

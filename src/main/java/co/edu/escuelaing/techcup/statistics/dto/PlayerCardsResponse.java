package co.edu.escuelaing.techcup.statistics.dto;

public record PlayerCardsResponse(
        Long playerId,
        Long tournamentId,
        long yellowCards,
        long redCards
) {
}

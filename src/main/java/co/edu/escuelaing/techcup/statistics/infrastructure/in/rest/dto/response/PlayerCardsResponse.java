package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

public record PlayerCardsResponse(
        String playerId,
        String tournamentId,
        long yellowCards,
        long redCards
) {
}

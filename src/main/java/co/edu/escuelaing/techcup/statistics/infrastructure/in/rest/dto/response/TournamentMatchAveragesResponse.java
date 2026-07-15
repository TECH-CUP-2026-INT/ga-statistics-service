package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

public record TournamentMatchAveragesResponse(
        String tournamentId,
        long matchesConsidered,
        double averageGoalsPerMatch,
        double averageFoulsPerMatch,
        double averageCardsPerMatch
) {
}

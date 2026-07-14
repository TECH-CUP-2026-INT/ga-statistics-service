package co.edu.escuelaing.techcup.statistics.dto;

public record TournamentMatchAveragesResponse(
        String tournamentId,
        long matchesConsidered,
        double averageGoalsPerMatch,
        double averageFoulsPerMatch,
        double averageCardsPerMatch
) {
}

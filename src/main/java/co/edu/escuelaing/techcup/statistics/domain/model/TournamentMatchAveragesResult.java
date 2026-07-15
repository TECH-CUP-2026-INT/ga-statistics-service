package co.edu.escuelaing.techcup.statistics.domain.model;

public record TournamentMatchAveragesResult(
        String tournamentId,
        long matchesConsidered,
        double averageGoalsPerMatch,
        double averageFoulsPerMatch,
        double averageCardsPerMatch
) {
}

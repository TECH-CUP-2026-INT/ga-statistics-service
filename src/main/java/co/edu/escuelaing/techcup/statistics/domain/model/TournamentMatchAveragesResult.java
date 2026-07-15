package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
public record TournamentMatchAveragesResult(
        UUID tournamentId,
        long matchesConsidered,
        double averageGoalsPerMatch,
        double averageFoulsPerMatch,
        double averageCardsPerMatch
) {}

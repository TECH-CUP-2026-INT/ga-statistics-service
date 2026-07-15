package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
public record TournamentMatchAveragesResponse(
        UUID tournamentId, long matchesConsidered,
        double averageGoalsPerMatch, double averageFoulsPerMatch, double averageCardsPerMatch
) {}

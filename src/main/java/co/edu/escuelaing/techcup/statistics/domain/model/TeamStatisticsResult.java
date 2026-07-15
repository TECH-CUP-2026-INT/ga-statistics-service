package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
public record TeamStatisticsResult(
        UUID teamId,
        UUID tournamentId,
        long matchesPlayed,
        long wins,
        long draws,
        long losses,
        long goalsFor,
        long goalsAgainst,
        long goalDifference,
        long points
) {}

package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
public record TeamGoalsResult(
        UUID teamId,
        UUID tournamentId,
        long goalsFor,
        long goalsAgainst,
        long goalDifference
) {}

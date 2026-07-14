package co.edu.escuelaing.techcup.statistics.dto;

public record TeamGoalsResponse(
        Long teamId,
        Long tournamentId,
        long goalsFor,
        long goalsAgainst,
        long goalDifference
) {
}

package co.edu.escuelaing.techcup.statistics.dto;

public record TeamGoalsResponse(
        String teamId,
        String tournamentId,
        long goalsFor,
        long goalsAgainst,
        long goalDifference
) {
}

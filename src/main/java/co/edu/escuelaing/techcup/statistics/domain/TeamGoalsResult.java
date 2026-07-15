package co.edu.escuelaing.techcup.statistics.domain;

public record TeamGoalsResult(
        String teamId,
        String tournamentId,
        long goalsFor,
        long goalsAgainst,
        long goalDifference
) {
}

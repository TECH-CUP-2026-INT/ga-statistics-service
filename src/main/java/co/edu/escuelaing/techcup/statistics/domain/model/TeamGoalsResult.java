package co.edu.escuelaing.techcup.statistics.domain.model;

public record TeamGoalsResult(
        String teamId,
        String tournamentId,
        long goalsFor,
        long goalsAgainst,
        long goalDifference
) {
}

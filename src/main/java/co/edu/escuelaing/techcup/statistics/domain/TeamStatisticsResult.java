package co.edu.escuelaing.techcup.statistics.domain;

public record TeamStatisticsResult(
        String teamId,
        String tournamentId,
        long matchesPlayed,
        long wins,
        long draws,
        long losses,
        long goalsFor,
        long goalsAgainst,
        long goalDifference,
        long points
) {
}

package co.edu.escuelaing.techcup.statistics.dto;

public record TeamStatisticsResponse(
        Long teamId,
        Long tournamentId,
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

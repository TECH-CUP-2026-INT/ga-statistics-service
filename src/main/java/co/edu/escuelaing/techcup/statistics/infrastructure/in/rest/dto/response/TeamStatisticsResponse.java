package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

public record TeamStatisticsResponse(
        String teamId, String tournamentId, long matchesPlayed, long wins, long draws, long losses,
        long goalsFor, long goalsAgainst, long goalDifference, long points
) {}

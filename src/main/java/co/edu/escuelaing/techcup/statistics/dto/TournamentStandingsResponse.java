package co.edu.escuelaing.techcup.statistics.dto;

import java.util.List;

public record TournamentStandingsResponse(
        String tournamentId,
        List<TeamStatisticsResponse> standings
) {
}

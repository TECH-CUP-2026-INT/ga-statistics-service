package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

import java.util.List;

public record TournamentStandingsResponse(
        String tournamentId,
        List<TeamStatisticsResponse> standings
) {
}

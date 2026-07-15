package co.edu.escuelaing.techcup.statistics.domain.model;

import java.util.List;

public record TournamentStandingsResult(
        String tournamentId,
        List<TeamStatisticsResult> standings
) {
}

package co.edu.escuelaing.techcup.statistics.domain;

import java.util.List;

public record TournamentStandingsResult(
        String tournamentId,
        List<TeamStatisticsResult> standings
) {
}

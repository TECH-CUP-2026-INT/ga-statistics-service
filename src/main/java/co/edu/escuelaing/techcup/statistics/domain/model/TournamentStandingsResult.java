package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
import java.util.List;

public record TournamentStandingsResult(
        UUID tournamentId,
        List<TeamStatisticsResult> standings
) {}

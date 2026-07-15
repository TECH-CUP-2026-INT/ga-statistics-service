package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
import java.util.List;

public record TournamentStandingsResponse(UUID tournamentId, List<TeamStatisticsResponse> standings) {}

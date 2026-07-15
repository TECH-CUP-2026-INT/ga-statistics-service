package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
public record TeamStatisticsResponse(
        UUID teamId, UUID tournamentId, long matchesPlayed, long wins, long draws, long losses,
        long goalsFor, long goalsAgainst, long goalDifference, long points
) {}

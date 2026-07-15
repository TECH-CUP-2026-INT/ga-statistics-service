package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
public record TeamGoalsResponse(UUID teamId, UUID tournamentId, long goalsFor, long goalsAgainst, long goalDifference) {}

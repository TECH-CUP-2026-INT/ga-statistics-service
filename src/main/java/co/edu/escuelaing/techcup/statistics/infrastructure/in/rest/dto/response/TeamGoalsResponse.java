package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

public record TeamGoalsResponse(String teamId, String tournamentId, long goalsFor, long goalsAgainst, long goalDifference) {}

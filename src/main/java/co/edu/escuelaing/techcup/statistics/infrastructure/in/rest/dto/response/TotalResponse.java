package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;

public record TotalResponse(String entityId, String tournamentId, String metric, long total, long eventsConsidered) {}

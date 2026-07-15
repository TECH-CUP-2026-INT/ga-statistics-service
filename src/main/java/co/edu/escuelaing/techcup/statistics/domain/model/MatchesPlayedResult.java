package co.edu.escuelaing.techcup.statistics.domain.model;

public record MatchesPlayedResult(
        String playerId,
        String tournamentId,
        long matchesPlayed
) {}

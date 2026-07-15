package co.edu.escuelaing.techcup.statistics.domain;

public record MatchesPlayedResult(
        String playerId,
        String tournamentId,
        long matchesPlayed
) {
}

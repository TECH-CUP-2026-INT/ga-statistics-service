package co.edu.escuelaing.techcup.statistics.dto;

public record MatchesPlayedResponse(
        String playerId,
        String tournamentId,
        long matchesPlayed
) {
}

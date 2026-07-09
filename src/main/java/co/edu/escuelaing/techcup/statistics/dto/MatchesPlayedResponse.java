package co.edu.escuelaing.techcup.statistics.dto;

public record MatchesPlayedResponse(
        Long playerId,
        Long tournamentId,
        long matchesPlayed
) {
}

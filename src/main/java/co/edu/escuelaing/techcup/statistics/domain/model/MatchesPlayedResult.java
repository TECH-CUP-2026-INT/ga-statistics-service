package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
public record MatchesPlayedResult(
        UUID playerId,
        UUID tournamentId,
        long matchesPlayed
) {}

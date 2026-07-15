package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response;
import java.util.UUID;
import java.time.LocalDateTime;
import java.util.List;

public record TournamentRecognitionResponse(
        UUID tournamentId,
        List<PlayerGoals> topScorers,
        long topScorersGoals,
        List<TeamGoalsAgainst> bestDefenseTeams,
        long bestDefenseGoalsAgainst,
        LocalDateTime generatedAt
) {
    public record PlayerGoals(UUID playerId, long goals) {}
    public record TeamGoalsAgainst(UUID teamId, long goalsAgainst) {}
}

package co.edu.escuelaing.techcup.statistics.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TournamentRecognitionResponse(
        String tournamentId,
        List<PlayerGoals> topScorers,
        long topScorersGoals,
        List<TeamGoalsAgainst> bestDefenseTeams,
        long bestDefenseGoalsAgainst,
        LocalDateTime generatedAt
) {
    public record PlayerGoals(String playerId, long goals) {
    }

    public record TeamGoalsAgainst(String teamId, long goalsAgainst) {
    }
}

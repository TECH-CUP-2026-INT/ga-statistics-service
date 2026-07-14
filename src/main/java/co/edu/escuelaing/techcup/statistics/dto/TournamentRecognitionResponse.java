package co.edu.escuelaing.techcup.statistics.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Reconocimientos GUARDADOS de un torneo (máximo goleador y malla menos
 * vencida). Son listas porque, si hay empate, se publican TODOS los
 * empatados (ej: dos jugadores con el mismo número de goles).
 */
public record TournamentRecognitionResponse(
        Long tournamentId,
        List<PlayerGoals> topScorers,
        long topScorersGoals,
        List<TeamGoalsAgainst> bestDefenseTeams,
        long bestDefenseGoalsAgainst,
        LocalDateTime generatedAt
) {
    public record PlayerGoals(Long playerId, long goals) {
    }

    public record TeamGoalsAgainst(Long teamId, long goalsAgainst) {
    }
}

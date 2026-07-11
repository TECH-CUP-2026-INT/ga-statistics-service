package co.edu.escuelaing.techcup.statistics.dto;

/**
 * Reconocimientos ("premios") calculados para un torneo:
 * - topScorer: el jugador con más goles (máximo goleador).
 * - bestDefense: el equipo con menos goles en contra (malla menos vencida).
 *
 * Si el torneo aún no tiene datos, los campos vienen en null.
 */
public record RecognitionResponse(
        Long tournamentId,
        TopScorer topScorer,
        BestDefense bestDefense
) {
    public record TopScorer(Long playerId, long goals) {
    }

    public record BestDefense(Long teamId, long goalsAgainst) {
    }
}

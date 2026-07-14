package co.edu.escuelaing.techcup.statistics.service;

import co.edu.escuelaing.techcup.statistics.dto.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.dto.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingType;
import co.edu.escuelaing.techcup.statistics.dto.RecognitionResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamStatisticsResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentStandingsResponse;

public interface StatisticsService {

    /**
     * Registra el resumen de un jugador en un partido finalizado.
     * Lo llama el servicio de Competencia.
     */
    void registerMatchStat(MatchStatEventRequest request);

    PlayerAverageResponse getAverageWinRate(Long playerId, Long tournamentId);

    PlayerAverageResponse getAverageGoals(Long playerId, Long tournamentId);

    PlayerAverageResponse getAverageFouls(Long playerId, Long tournamentId);

    PlayerAverageResponse getAverageMinutesPlayed(Long playerId, Long tournamentId);

    MatchesPlayedResponse getMatchesPlayed(Long playerId, Long tournamentId);

    RankingResponse getRanking(RankingType type, Long tournamentId, int limit);

    /** Estadísticas generales del torneo: tabla de posiciones completa. */
    TournamentStandingsResponse getTournamentStandings(Long tournamentId);

    /** Estadísticas de un equipo específico dentro del torneo activo. */
    TeamStatisticsResponse getTeamStatisticsInActiveTournament(Long teamId);

    /** Reconocimientos del torneo: máximo goleador y malla menos vencida. */
    RecognitionResponse getTournamentRecognitions(Long tournamentId);
}

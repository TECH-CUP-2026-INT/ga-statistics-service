package co.edu.escuelaing.techcup.statistics.service;

import co.edu.escuelaing.techcup.statistics.dto.CardsTotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.GoalkeeperRankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchResultResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.dto.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerCardsResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingType;
import co.edu.escuelaing.techcup.statistics.dto.TeamAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamGoalsResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamMatchRecordResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamStatisticsResponse;
import co.edu.escuelaing.techcup.statistics.dto.TotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentMatchAveragesResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentRecognitionResponse;
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

    /**
     * Calcula y GUARDA el reconocimiento del torneo (máximo goleador y
     * malla menos vencida). Lo dispara el servicio de Torneos al finalizar
     * el torneo. Si ya existía uno para este torneo, lo reemplaza.
     */
    TournamentRecognitionResponse generateTournamentRecognitions(Long tournamentId);

    /**
     * Consulta el reconocimiento ya guardado de un torneo. Lanza
     * RecognitionNotFoundException si aún no se ha generado.
     */
    TournamentRecognitionResponse getTournamentRecognitions(Long tournamentId);

    /** Ranking de porteros por menos goles recibidos (valla menos vencida). */
    GoalkeeperRankingResponse getGoalkeeperRanking(Long tournamentId, int limit);

    TotalResponse getPlayerTotalAssists(Long playerId, Long tournamentId);

    // ---------- Jugador: totales y tarjetas ----------

    TotalResponse getPlayerTotalGoals(Long playerId, Long tournamentId);

    TotalResponse getPlayerTotalFouls(Long playerId, Long tournamentId);

    PlayerCardsResponse getPlayerCards(Long playerId, Long tournamentId);

    // ---------- Equipo ----------

    TeamMatchRecordResponse getTeamMatchRecord(Long teamId, Long tournamentId);

    TeamAverageResponse getTeamAverageGoals(Long teamId, Long tournamentId);

    TeamAverageResponse getTeamAverageFouls(Long teamId, Long tournamentId);

    TotalResponse getTeamTotalFouls(Long teamId, Long tournamentId);

    TeamGoalsResponse getTeamGoals(Long teamId, Long tournamentId);

    // ---------- Torneo (agregados por partido) ----------

    TournamentMatchAveragesResponse getTournamentMatchAverages(Long tournamentId);

    CardsTotalResponse getTournamentCardsTotal(Long tournamentId);

    // ---------- Partido ----------

    CardsTotalResponse getMatchCardsTotal(Long matchId);

    MatchResultResponse getMatchResult(Long matchId);
}

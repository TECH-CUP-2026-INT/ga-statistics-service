package co.edu.escuelaing.techcup.statistics.service;

import co.edu.escuelaing.techcup.statistics.dto.CardsTotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.GoalkeeperRankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchResultResponse;
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
import co.edu.escuelaing.techcup.statistics.domain.PlayerMatchStatistic;

public interface StatisticsService {

    /**
     * Registra el resumen de un jugador en un partido finalizado.
     * Recibe un objeto de DOMINIO, no el DTO de entrada de la web -- la
     * conversion Request -&gt; dominio la hace el controlador con el mapper.
     */
    void registerMatchStat(PlayerMatchStatistic statistic);

    PlayerAverageResponse getAverageWinRate(String playerId, String tournamentId);

    PlayerAverageResponse getAverageGoals(String playerId, String tournamentId);

    PlayerAverageResponse getAverageFouls(String playerId, String tournamentId);

    PlayerAverageResponse getAverageMinutesPlayed(String playerId, String tournamentId);

    MatchesPlayedResponse getMatchesPlayed(String playerId, String tournamentId);

    RankingResponse getRanking(RankingType type, String tournamentId, int limit);

    /** Estadísticas generales del torneo: tabla de posiciones completa. */
    TournamentStandingsResponse getTournamentStandings(String tournamentId);

    /** Estadísticas de un equipo específico dentro del torneo activo. */
    TeamStatisticsResponse getTeamStatisticsInActiveTournament(String teamId);

    /**
     * Calcula y GUARDA el reconocimiento del torneo (máximo goleador y
     * malla menos vencida). Lo dispara el servicio de Torneos al finalizar
     * el torneo. Si ya existía uno para este torneo, lo reemplaza.
     */
    TournamentRecognitionResponse generateTournamentRecognitions(String tournamentId);

    /**
     * Consulta el reconocimiento ya guardado de un torneo. Lanza
     * RecognitionNotFoundException si aún no se ha generado.
     */
    TournamentRecognitionResponse getTournamentRecognitions(String tournamentId);

    /** Ranking de porteros por menos goles recibidos (valla menos vencida). */
    GoalkeeperRankingResponse getGoalkeeperRanking(String tournamentId, int limit);

    TotalResponse getPlayerTotalAssists(String playerId, String tournamentId);

    // ---------- Jugador: totales y tarjetas ----------

    TotalResponse getPlayerTotalGoals(String playerId, String tournamentId);

    TotalResponse getPlayerTotalFouls(String playerId, String tournamentId);

    PlayerCardsResponse getPlayerCards(String playerId, String tournamentId);

    // ---------- Equipo ----------

    TeamMatchRecordResponse getTeamMatchRecord(String teamId, String tournamentId);

    TeamAverageResponse getTeamAverageGoals(String teamId, String tournamentId);

    TeamAverageResponse getTeamAverageFouls(String teamId, String tournamentId);

    TotalResponse getTeamTotalFouls(String teamId, String tournamentId);

    TeamGoalsResponse getTeamGoals(String teamId, String tournamentId);

    // ---------- Torneo (agregados por partido) ----------

    TournamentMatchAveragesResponse getTournamentMatchAverages(String tournamentId);

    CardsTotalResponse getTournamentCardsTotal(String tournamentId);

    // ---------- Partido ----------

    CardsTotalResponse getMatchCardsTotal(String matchId);

    MatchResultResponse getMatchResult(String matchId);
}

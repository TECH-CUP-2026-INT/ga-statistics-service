package co.edu.escuelaing.techcup.statistics.service;

import co.edu.escuelaing.techcup.statistics.domain.CardsTotalResult;
import co.edu.escuelaing.techcup.statistics.domain.GoalkeeperRankingResult;
import co.edu.escuelaing.techcup.statistics.domain.MatchResultResult;
import co.edu.escuelaing.techcup.statistics.domain.MatchesPlayedResult;
import co.edu.escuelaing.techcup.statistics.domain.PlayerAverageResult;
import co.edu.escuelaing.techcup.statistics.domain.PlayerCardsResult;
import co.edu.escuelaing.techcup.statistics.domain.PlayerMatchStatistic;
import co.edu.escuelaing.techcup.statistics.domain.RankingResult;
import co.edu.escuelaing.techcup.statistics.domain.TeamAverageResult;
import co.edu.escuelaing.techcup.statistics.domain.TeamGoalsResult;
import co.edu.escuelaing.techcup.statistics.domain.TeamMatchRecordResult;
import co.edu.escuelaing.techcup.statistics.domain.TeamStatisticsResult;
import co.edu.escuelaing.techcup.statistics.domain.TotalResult;
import co.edu.escuelaing.techcup.statistics.domain.TournamentMatchAveragesResult;
import co.edu.escuelaing.techcup.statistics.domain.TournamentRecognitionRecord;
import co.edu.escuelaing.techcup.statistics.domain.TournamentStandingsResult;
import co.edu.escuelaing.techcup.statistics.dto.RankingType;

/**
 * IMPORTANTE (separacion de capas): esta interfaz solo habla en terminos de
 * objetos de DOMINIO -- ni en la entrada (PlayerMatchStatistic, no el DTO
 * web) ni en la salida (XxxResult, no el DTO de respuesta). El controller es
 * quien mapea hacia/desde los DTOs con MapStruct.
 */
public interface StatisticsService {

    void registerMatchStat(PlayerMatchStatistic statistic);

    PlayerAverageResult getAverageWinRate(String playerId, String tournamentId);

    PlayerAverageResult getAverageGoals(String playerId, String tournamentId);

    PlayerAverageResult getAverageFouls(String playerId, String tournamentId);

    PlayerAverageResult getAverageMinutesPlayed(String playerId, String tournamentId);

    MatchesPlayedResult getMatchesPlayed(String playerId, String tournamentId);

    RankingResult getRanking(RankingType type, String tournamentId, int limit);

    TournamentStandingsResult getTournamentStandings(String tournamentId);

    TeamStatisticsResult getTeamStatisticsInActiveTournament(String teamId);

    TournamentRecognitionRecord generateTournamentRecognitions(String tournamentId);

    TournamentRecognitionRecord getTournamentRecognitions(String tournamentId);

    GoalkeeperRankingResult getGoalkeeperRanking(String tournamentId, int limit);

    TotalResult getPlayerTotalAssists(String playerId, String tournamentId);

    TotalResult getPlayerTotalGoals(String playerId, String tournamentId);

    TotalResult getPlayerTotalFouls(String playerId, String tournamentId);

    PlayerCardsResult getPlayerCards(String playerId, String tournamentId);

    TeamMatchRecordResult getTeamMatchRecord(String teamId, String tournamentId);

    TeamAverageResult getTeamAverageGoals(String teamId, String tournamentId);

    TeamAverageResult getTeamAverageFouls(String teamId, String tournamentId);

    TotalResult getTeamTotalFouls(String teamId, String tournamentId);

    TeamGoalsResult getTeamGoals(String teamId, String tournamentId);

    TournamentMatchAveragesResult getTournamentMatchAverages(String tournamentId);

    CardsTotalResult getTournamentCardsTotal(String tournamentId);

    CardsTotalResult getMatchCardsTotal(String matchId);

    MatchResultResult getMatchResult(String matchId);
}

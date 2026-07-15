package co.edu.escuelaing.techcup.statistics.domain.service.ports.in;

import co.edu.escuelaing.techcup.statistics.domain.model.CardsTotalResult;
import co.edu.escuelaing.techcup.statistics.domain.model.GoalkeeperRankingResult;
import co.edu.escuelaing.techcup.statistics.domain.model.MatchResultResult;
import co.edu.escuelaing.techcup.statistics.domain.model.MatchesPlayedResult;
import co.edu.escuelaing.techcup.statistics.domain.model.PlayerAverageResult;
import co.edu.escuelaing.techcup.statistics.domain.model.PlayerCardsResult;
import co.edu.escuelaing.techcup.statistics.domain.model.PlayerMatchStatistic;
import co.edu.escuelaing.techcup.statistics.domain.model.RankingResult;
import co.edu.escuelaing.techcup.statistics.domain.model.RankingType;
import co.edu.escuelaing.techcup.statistics.domain.model.TeamAverageResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TeamGoalsResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TeamMatchRecordResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TeamStatisticsResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TotalResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TournamentMatchAveragesResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TournamentRecognitionRecord;
import co.edu.escuelaing.techcup.statistics.domain.model.TournamentStandingsResult;

/**
 * Puerto de entrada del hexágono: define los casos de uso del servicio
 * de estadísticas. Trabaja exclusivamente con objetos del dominio.
 */
public interface StatisticsUseCase {

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

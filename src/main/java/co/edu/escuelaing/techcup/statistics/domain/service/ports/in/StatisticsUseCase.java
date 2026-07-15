package co.edu.escuelaing.techcup.statistics.domain.service.ports.in;
import java.util.UUID;
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
 * Puerto de entrada del hexÃ¡gono: define los casos de uso del servicio
 * de estadÃ­sticas. Trabaja exclusivamente con objetos del dominio.
 */
public interface StatisticsUseCase {

    void registerMatchStat(PlayerMatchStatistic statistic);

    PlayerAverageResult getAverageWinRate(UUID playerId, UUID tournamentId);

    PlayerAverageResult getAverageGoals(UUID playerId, UUID tournamentId);

    PlayerAverageResult getAverageFouls(UUID playerId, UUID tournamentId);

    PlayerAverageResult getAverageMinutesPlayed(UUID playerId, UUID tournamentId);

    MatchesPlayedResult getMatchesPlayed(UUID playerId, UUID tournamentId);

    RankingResult getRanking(RankingType type, UUID tournamentId, int limit);

    TournamentStandingsResult getTournamentStandings(UUID tournamentId);

    TeamStatisticsResult getTeamStatistics(UUID teamId, UUID tournamentId);

    TournamentRecognitionRecord generateTournamentRecognitions(UUID tournamentId);

    TournamentRecognitionRecord getTournamentRecognitions(UUID tournamentId);

    GoalkeeperRankingResult getGoalkeeperRanking(UUID tournamentId, int limit);

    TotalResult getPlayerTotalAssists(UUID playerId, UUID tournamentId);

    TotalResult getPlayerTotalGoals(UUID playerId, UUID tournamentId);

    TotalResult getPlayerTotalFouls(UUID playerId, UUID tournamentId);

    PlayerCardsResult getPlayerCards(UUID playerId, UUID tournamentId);

    TeamMatchRecordResult getTeamMatchRecord(UUID teamId, UUID tournamentId);

    TeamAverageResult getTeamAverageGoals(UUID teamId, UUID tournamentId);

    TeamAverageResult getTeamAverageFouls(UUID teamId, UUID tournamentId);

    TotalResult getTeamTotalFouls(UUID teamId, UUID tournamentId);

    TeamGoalsResult getTeamGoals(UUID teamId, UUID tournamentId);

    TournamentMatchAveragesResult getTournamentMatchAverages(UUID tournamentId);

    CardsTotalResult getTournamentCardsTotal(UUID tournamentId);

    CardsTotalResult getMatchCardsTotal(UUID matchId);

    MatchResultResult getMatchResult(UUID matchId);
}

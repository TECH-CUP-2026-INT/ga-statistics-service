package co.edu.escuelaing.techcup.statistics.repository;

import co.edu.escuelaing.techcup.statistics.entity.PlayerMatchStat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repositorio Mongo: NO hay agregaciones (AVG/SUM/GROUP BY) aquí como en SQL.
 * Este repositorio solo trae los documentos crudos que hagan falta; los
 * promedios, sumas y agrupaciones se calculan en StatisticsServiceImpl con
 * streams de Java.
 */
public interface PlayerMatchStatRepository extends MongoRepository<PlayerMatchStat, String> {

    boolean existsByPlayerIdAndMatchId(String playerId, String matchId);

    List<PlayerMatchStat> findByPlayerId(String playerId);

    List<PlayerMatchStat> findByPlayerIdAndTournamentId(String playerId, String tournamentId);

    List<PlayerMatchStat> findByTeamId(String teamId);

    List<PlayerMatchStat> findByTeamIdAndTournamentId(String teamId, String tournamentId);

    List<PlayerMatchStat> findByTournamentId(String tournamentId);

    List<PlayerMatchStat> findByMatchId(String matchId);
}

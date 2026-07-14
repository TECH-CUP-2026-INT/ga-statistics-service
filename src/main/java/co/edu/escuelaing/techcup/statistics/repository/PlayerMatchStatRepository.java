package co.edu.escuelaing.techcup.statistics.repository;

import co.edu.escuelaing.techcup.statistics.entity.PlayerMatchStat;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Repositorio Mongo: NO hay agregaciones (AVG/SUM/GROUP BY) aquí como en SQL.
 * Este repositorio solo trae los documentos crudos que hagan falta; los
 * promedios, sumas y agrupaciones se calculan en StatisticsServiceImpl con
 * streams de Java. Es un poco menos eficiente que dejarlo en la base de
 * datos, pero mucho más simple de leer y mantener para el volumen de datos
 * de un torneo universitario.
 */
public interface PlayerMatchStatRepository extends MongoRepository<PlayerMatchStat, String> {

    boolean existsByPlayerIdAndMatchId(Long playerId, Long matchId);

    List<PlayerMatchStat> findByPlayerId(Long playerId);

    List<PlayerMatchStat> findByPlayerIdAndTournamentId(Long playerId, Long tournamentId);

    List<PlayerMatchStat> findByTeamId(Long teamId);

    List<PlayerMatchStat> findByTeamIdAndTournamentId(Long teamId, Long tournamentId);

    List<PlayerMatchStat> findByTournamentId(Long tournamentId);

    List<PlayerMatchStat> findByMatchId(Long matchId);
}

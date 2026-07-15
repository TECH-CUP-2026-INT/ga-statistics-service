package co.edu.escuelaing.techcup.statistics.domain.service.ports.out;

import co.edu.escuelaing.techcup.statistics.domain.model.PlayerMatchStatistic;

import java.util.List;

/**
 * Puerto de salida: lo que la aplicacion necesita para persistir y consultar
 * estadisticas de jugador, expresado SOLO en terminos de dominio. Quien lo
 * implemente (infrastructure/persistence) decide si usa Mongo, SQL, etc.
 */
public interface PlayerMatchStatRepositoryPort {

    boolean existsByPlayerIdAndMatchId(String playerId, String matchId);

    void save(PlayerMatchStatistic statistic);

    List<PlayerMatchStatistic> findByPlayerId(String playerId);

    List<PlayerMatchStatistic> findByPlayerIdAndTournamentId(String playerId, String tournamentId);

    List<PlayerMatchStatistic> findByTeamId(String teamId);

    List<PlayerMatchStatistic> findByTeamIdAndTournamentId(String teamId, String tournamentId);

    List<PlayerMatchStatistic> findByTournamentId(String tournamentId);

    List<PlayerMatchStatistic> findByMatchId(String matchId);

    List<PlayerMatchStatistic> findAll();
}

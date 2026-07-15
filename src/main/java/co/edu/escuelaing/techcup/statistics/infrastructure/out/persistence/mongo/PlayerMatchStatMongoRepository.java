package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo;

import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.PlayerMatchStatDocument;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Interfaz tecnica de Spring Data. Uso interno de infrastructure -- ningun
 * otro paquete deberia inyectar esto directamente (usan el puerto en su
 * lugar).
 */
public interface PlayerMatchStatMongoRepository extends MongoRepository<PlayerMatchStatDocument, String> {

    boolean existsByPlayerIdAndMatchId(String playerId, String matchId);

    List<PlayerMatchStatDocument> findByPlayerId(String playerId);

    List<PlayerMatchStatDocument> findByPlayerIdAndTournamentId(String playerId, String tournamentId);

    List<PlayerMatchStatDocument> findByTeamId(String teamId);

    List<PlayerMatchStatDocument> findByTeamIdAndTournamentId(String teamId, String tournamentId);

    List<PlayerMatchStatDocument> findByTournamentId(String tournamentId);

    List<PlayerMatchStatDocument> findByMatchId(String matchId);
}

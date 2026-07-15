package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlayerMatchStatRepository extends MongoRepository<PlayerMatchStatDocument, String> {

    boolean existsByPlayerIdAndMatchId(String playerId, String matchId);

    List<PlayerMatchStatDocument> findByPlayerId(String playerId);

    List<PlayerMatchStatDocument> findByPlayerIdAndTournamentId(String playerId, String tournamentId);

    List<PlayerMatchStatDocument> findByTeamId(String teamId);

    List<PlayerMatchStatDocument> findByTeamIdAndTournamentId(String teamId, String tournamentId);

    List<PlayerMatchStatDocument> findByTournamentId(String tournamentId);

    List<PlayerMatchStatDocument> findByMatchId(String matchId);
}

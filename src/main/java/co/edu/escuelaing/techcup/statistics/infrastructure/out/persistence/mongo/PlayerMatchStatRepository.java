package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlayerMatchStatRepository extends MongoRepository<PlayerMatchStatDocument, String> {

    boolean existsByPlayerIdAndMatchId(UUID playerId, UUID matchId);

    List<PlayerMatchStatDocument> findByPlayerId(UUID playerId);

    List<PlayerMatchStatDocument> findByPlayerIdAndTournamentId(UUID playerId, UUID tournamentId);

    List<PlayerMatchStatDocument> findByTeamId(UUID teamId);

    List<PlayerMatchStatDocument> findByTeamIdAndTournamentId(UUID teamId, UUID tournamentId);

    List<PlayerMatchStatDocument> findByTournamentId(UUID tournamentId);

    List<PlayerMatchStatDocument> findByMatchId(UUID matchId);
}

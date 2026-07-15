package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TournamentRecognitionRepository extends MongoRepository<TournamentRecognitionDocument, String> {
    Optional<TournamentRecognitionDocument> findByTournamentId(String tournamentId);
}

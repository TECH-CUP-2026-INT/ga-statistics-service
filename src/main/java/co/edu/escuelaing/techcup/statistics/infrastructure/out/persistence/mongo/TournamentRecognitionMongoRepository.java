package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo;

import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.TournamentRecognitionDocument;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TournamentRecognitionMongoRepository extends MongoRepository<TournamentRecognitionDocument, String> {
    Optional<TournamentRecognitionDocument> findByTournamentId(String tournamentId);
}

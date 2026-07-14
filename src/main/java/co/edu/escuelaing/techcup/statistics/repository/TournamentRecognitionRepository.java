package co.edu.escuelaing.techcup.statistics.repository;

import co.edu.escuelaing.techcup.statistics.entity.TournamentRecognition;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TournamentRecognitionRepository extends MongoRepository<TournamentRecognition, String> {
    Optional<TournamentRecognition> findByTournamentId(String tournamentId);
}

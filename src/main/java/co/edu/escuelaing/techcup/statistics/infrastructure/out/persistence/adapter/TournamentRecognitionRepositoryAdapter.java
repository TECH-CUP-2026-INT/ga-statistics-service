package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.statistics.domain.model.TournamentRecognitionRecord;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.out.TournamentRecognitionRepositoryPort;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.TournamentRecognitionDocument;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mapper.TournamentRecognitionDocumentMapper;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.TournamentRecognitionMongoRepository;

import org.springframework.stereotype.Component;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TournamentRecognitionRepositoryAdapter implements TournamentRecognitionRepositoryPort {

    private final TournamentRecognitionMongoRepository mongoRepository;
    private final TournamentRecognitionDocumentMapper mapper;

    @Override
    public Optional<TournamentRecognitionRecord> findByTournamentId(String tournamentId) {
        return mongoRepository.findByTournamentId(tournamentId).map(mapper::toDomain);
    }

    @Override
    public TournamentRecognitionRecord save(TournamentRecognitionRecord recognition) {
        TournamentRecognitionDocument document = mapper.toDocument(recognition);

        // Upsert por tournamentId: si ya existia uno, reusamos su _id de
        // Mongo para que el save() lo REEMPLACE en vez de duplicarlo. Este
        // detalle es 100% de infraestructura, la aplicacion no lo sabe.
        mongoRepository.findByTournamentId(recognition.getTournamentId())
                .ifPresent(existing -> document.setId(existing.getId()));

        TournamentRecognitionDocument saved = mongoRepository.save(document);
        return mapper.toDomain(saved);
    }
}

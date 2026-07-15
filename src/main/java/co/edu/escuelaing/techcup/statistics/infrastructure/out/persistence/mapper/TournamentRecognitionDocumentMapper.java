package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.statistics.domain.model.TournamentRecognitionRecord;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.TournamentRecognitionDocument;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Convierte entre el documento de Mongo y el dominio. Solo lo usa el adaptador de persistencia. */
@Mapper(componentModel = "spring")
public interface TournamentRecognitionDocumentMapper {

    TournamentRecognitionRecord toDomain(TournamentRecognitionDocument document);

    @Mapping(target = "id", ignore = true)
    TournamentRecognitionDocument toDocument(TournamentRecognitionRecord domain);
}

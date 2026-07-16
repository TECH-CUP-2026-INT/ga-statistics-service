package co.edu.escuelaing.techcup.statistics.application.mapper;

import co.edu.escuelaing.techcup.statistics.domain.model.TournamentRecognitionRecord;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.TournamentRecognitionDocument;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TournamentRecognitionMapper {

    TournamentRecognitionRecord toDomain(TournamentRecognitionDocument document);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "generatedAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "lastModifiedBy", ignore = true)
    TournamentRecognitionDocument toDocument(TournamentRecognitionRecord domain);
}

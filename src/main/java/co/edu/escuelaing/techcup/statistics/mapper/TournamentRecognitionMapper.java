package co.edu.escuelaing.techcup.statistics.mapper;

import co.edu.escuelaing.techcup.statistics.domain.TournamentRecognitionRecord;
import co.edu.escuelaing.techcup.statistics.entity.TournamentRecognition;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Convierte entre el documento de MongoDB (TournamentRecognition) y el
 * objeto de dominio (TournamentRecognitionRecord). El service solo conoce
 * el objeto de dominio.
 */
@Mapper(componentModel = "spring")
public interface TournamentRecognitionMapper {

    TournamentRecognitionRecord toDomain(TournamentRecognition document);

    @Mapping(target = "id", ignore = true)
    TournamentRecognition toDocument(TournamentRecognitionRecord domain);
}

package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mapper;

import co.edu.escuelaing.techcup.statistics.domain.model.PlayerMatchStatistic;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.PlayerMatchStatDocument;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/** Convierte entre el documento de Mongo y el dominio. Solo lo usa el adaptador de persistencia. */
@Mapper(componentModel = "spring")
public interface PlayerMatchStatDocumentMapper {

    PlayerMatchStatistic toDomain(PlayerMatchStatDocument document);

    List<PlayerMatchStatistic> toDomainList(List<PlayerMatchStatDocument> documents);

    @Mapping(target = "id", ignore = true)
    PlayerMatchStatDocument toDocument(PlayerMatchStatistic domain);
}

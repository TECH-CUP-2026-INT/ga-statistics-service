package co.edu.escuelaing.techcup.statistics.application.mapper;

import co.edu.escuelaing.techcup.statistics.domain.model.PlayerMatchStatistic;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.request.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.PlayerMatchStatDocument;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayerMatchStatMapper {

    PlayerMatchStatistic toDomain(PlayerMatchStatDocument document);

    List<PlayerMatchStatistic> toDomainList(List<PlayerMatchStatDocument> documents);

    @Mapping(target = "id", ignore = true)
    PlayerMatchStatDocument toDocument(PlayerMatchStatistic domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "goals", source = "goals", defaultValue = "0")
    @Mapping(target = "yellowCards", source = "yellowCards", defaultValue = "0")
    @Mapping(target = "redCards", source = "redCards", defaultValue = "0")
    @Mapping(target = "foulsCommitted", source = "foulsCommitted", defaultValue = "0")
    @Mapping(target = "minutesPlayed", source = "minutesPlayed", defaultValue = "0")
    @Mapping(target = "assists", source = "assists", defaultValue = "0")
    @Mapping(target = "goalkeeper", expression = "java(request.goalkeeper() != null && request.goalkeeper())")
    PlayerMatchStatistic toDomain(MatchStatEventRequest request);
}

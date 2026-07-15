package co.edu.escuelaing.techcup.statistics.mapper;

import co.edu.escuelaing.techcup.statistics.domain.PlayerMatchStatistic;
import co.edu.escuelaing.techcup.statistics.dto.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.entity.PlayerMatchStat;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Convierte entre las 3 representaciones de "un jugador en un partido":
 * - PlayerMatchStat: documento de MongoDB (capa de persistencia)
 * - PlayerMatchStatistic: objeto de dominio (capa de negocio)
 * - MatchStatEventRequest: DTO de entrada (capa web)
 *
 * El service SOLO conoce PlayerMatchStatistic; nunca ve el documento ni el
 * DTO de entrada directamente.
 */
@Mapper(componentModel = "spring")
public interface PlayerMatchStatMapper {

    PlayerMatchStatistic toDomain(PlayerMatchStat document);

    List<PlayerMatchStatistic> toDomainList(List<PlayerMatchStat> documents);

    @Mapping(target = "id", ignore = true)
    PlayerMatchStat toDocument(PlayerMatchStatistic domain);

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

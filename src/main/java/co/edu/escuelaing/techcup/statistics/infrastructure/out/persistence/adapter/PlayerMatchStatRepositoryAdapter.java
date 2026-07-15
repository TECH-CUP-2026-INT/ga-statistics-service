package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.adapter;

import co.edu.escuelaing.techcup.statistics.domain.model.PlayerMatchStatistic;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.out.PlayerMatchStatRepositoryPort;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mapper.PlayerMatchStatDocumentMapper;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.PlayerMatchStatMongoRepository;

import org.springframework.stereotype.Component;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * Adaptador de salida: implementa el puerto usando MongoDB. Es el UNICO
 * lugar (junto con su mapper) que conoce PlayerMatchStatDocument.
 */
@Component
@RequiredArgsConstructor
public class PlayerMatchStatRepositoryAdapter implements PlayerMatchStatRepositoryPort {

    private final PlayerMatchStatMongoRepository mongoRepository;
    private final PlayerMatchStatDocumentMapper mapper;

    @Override
    public boolean existsByPlayerIdAndMatchId(String playerId, String matchId) {
        return mongoRepository.existsByPlayerIdAndMatchId(playerId, matchId);
    }

    @Override
    public void save(PlayerMatchStatistic statistic) {
        mongoRepository.save(mapper.toDocument(statistic));
    }

    @Override
    public List<PlayerMatchStatistic> findByPlayerId(String playerId) {
        return mapper.toDomainList(mongoRepository.findByPlayerId(playerId));
    }

    @Override
    public List<PlayerMatchStatistic> findByPlayerIdAndTournamentId(String playerId, String tournamentId) {
        return mapper.toDomainList(mongoRepository.findByPlayerIdAndTournamentId(playerId, tournamentId));
    }

    @Override
    public List<PlayerMatchStatistic> findByTeamId(String teamId) {
        return mapper.toDomainList(mongoRepository.findByTeamId(teamId));
    }

    @Override
    public List<PlayerMatchStatistic> findByTeamIdAndTournamentId(String teamId, String tournamentId) {
        return mapper.toDomainList(mongoRepository.findByTeamIdAndTournamentId(teamId, tournamentId));
    }

    @Override
    public List<PlayerMatchStatistic> findByTournamentId(String tournamentId) {
        return mapper.toDomainList(mongoRepository.findByTournamentId(tournamentId));
    }

    @Override
    public List<PlayerMatchStatistic> findByMatchId(String matchId) {
        return mapper.toDomainList(mongoRepository.findByMatchId(matchId));
    }

    @Override
    public List<PlayerMatchStatistic> findAll() {
        return mapper.toDomainList(mongoRepository.findAll());
    }
}

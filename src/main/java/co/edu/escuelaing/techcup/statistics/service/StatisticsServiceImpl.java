package co.edu.escuelaing.techcup.statistics.service;

import co.edu.escuelaing.techcup.statistics.dto.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.dto.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingEntryResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingType;
import co.edu.escuelaing.techcup.statistics.entity.PlayerMatchStat;
import co.edu.escuelaing.techcup.statistics.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.repository.PlayerMatchStatRepository;
import co.edu.escuelaing.techcup.statistics.repository.PlayerMatchStatRepository.RankingRow;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final PlayerMatchStatRepository repository;

    @Override
    @Transactional
    public void registerMatchStat(MatchStatEventRequest request) {
        if (repository.existsByPlayerIdAndMatchId(request.playerId(), request.matchId())) {
            throw new DuplicateMatchStatException(request.playerId(), request.matchId());
        }

        PlayerMatchStat stat = PlayerMatchStat.builder()
                .playerId(request.playerId())
                .teamId(request.teamId())
                .matchId(request.matchId())
                .tournamentId(request.tournamentId())
                .result(request.result())
                .goals(defaultZero(request.goals()))
                .yellowCards(defaultZero(request.yellowCards()))
                .redCards(defaultZero(request.redCards()))
                .foulsCommitted(defaultZero(request.foulsCommitted()))
                .minutesPlayed(defaultZero(request.minutesPlayed()))
                .build();

        repository.save(stat);
    }

   @Override
    public PlayerAverageResponse getAverageWinRate(Long playerId, Long tournamentId) {
        long played = repository.countMatchesPlayed(playerId, tournamentId);
        double winRatePercentage = 0.0;
        if (played > 0) {
            long won = repository.countMatchesWon(playerId, tournamentId);
            winRatePercentage = round((won * 100.0) / played);
        }
        return new PlayerAverageResponse(playerId, tournamentId, "averageWinRatePercentage",
                winRatePercentage, played);
    }

    @Override
    public PlayerAverageResponse getAverageGoals(Long playerId, Long tournamentId) {
        long played = repository.countMatchesPlayed(playerId, tournamentId);
        double average = round(repository.averageGoals(playerId, tournamentId));
        return new PlayerAverageResponse(playerId, tournamentId, "averageGoals", average, played);
    }

    @Override
    public PlayerAverageResponse getAverageFouls(Long playerId, Long tournamentId) {
        long played = repository.countMatchesPlayed(playerId, tournamentId);
        double average = round(repository.averageFouls(playerId, tournamentId));
        return new PlayerAverageResponse(playerId, tournamentId, "averageFouls", average, played);
    }

    @Override
    public PlayerAverageResponse getAverageMinutesPlayed(Long playerId, Long tournamentId) {
        long played = repository.countMatchesPlayed(playerId, tournamentId);
        double average = round(repository.averageMinutesPlayed(playerId, tournamentId));
        return new PlayerAverageResponse(playerId, tournamentId, "averageMinutesPlayed", average, played);
    }

    @Override
    public MatchesPlayedResponse getMatchesPlayed(Long playerId, Long tournamentId) {
        long played = repository.countMatchesPlayed(playerId, tournamentId);
        return new MatchesPlayedResponse(playerId, tournamentId, played);
    }

    @Override
    public RankingResponse getRanking(RankingType type, Long tournamentId, int limit) {
        Pageable topN = PageRequest.of(0, Math.max(limit, 1));

        List<RankingRow> rows = switch (type) {
            case GOALS -> repository.findGoalsRanking(tournamentId, topN);
            case WINS -> repository.findWinsRanking(tournamentId, topN);
            case FOULS -> repository.findFairPlayRanking(tournamentId, topN);
            case MINUTES -> repository.findMinutesRanking(tournamentId, topN);
        };

        List<RankingEntryResponse> entries = new java.util.ArrayList<>();
        int position = 1;
        for (RankingRow row : rows) {
            entries.add(new RankingEntryResponse(position++, row.getPlayerId(), row.getValue()));
        }

        return new RankingResponse(type.name(), tournamentId, entries);
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

package co.edu.escuelaing.techcup.statistics.service;

import co.edu.escuelaing.techcup.statistics.client.TournamentClient;
import co.edu.escuelaing.techcup.statistics.dto.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.dto.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingEntryResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingType;
import co.edu.escuelaing.techcup.statistics.dto.RecognitionResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamStatisticsResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentStandingsResponse;
import co.edu.escuelaing.techcup.statistics.entity.MatchResult;
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
    private final TournamentClient tournamentClient;

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

    @Override
    public TournamentStandingsResponse getTournamentStandings(Long tournamentId) {
        List<Long> teamIds = repository.findDistinctTeamIds(tournamentId);

        List<TeamStatisticsResponse> standings = teamIds.stream()
                .map(teamId -> buildTeamStatistics(teamId, tournamentId))
                .sorted(
                        java.util.Comparator.comparingLong(TeamStatisticsResponse::points).reversed()
                                .thenComparing(java.util.Comparator.comparingLong(
                                        TeamStatisticsResponse::goalDifference).reversed())
                                .thenComparing(java.util.Comparator.comparingLong(
                                        TeamStatisticsResponse::goalsFor).reversed())
                )
                .toList();

        return new TournamentStandingsResponse(tournamentId, standings);
    }

    @Override
    public TeamStatisticsResponse getTeamStatisticsInActiveTournament(Long teamId) {
        Long activeTournamentId = tournamentClient.getActiveTournamentId();
        return buildTeamStatistics(teamId, activeTournamentId);
    }

    @Override
    public RecognitionResponse getTournamentRecognitions(Long tournamentId) {
        RecognitionResponse.TopScorer topScorer = repository
                .findGoalsRanking(tournamentId, PageRequest.of(0, 1))
                .stream()
                .findFirst()
                .map(row -> new RecognitionResponse.TopScorer(row.getPlayerId(), row.getValue()))
                .orElse(null);

        RecognitionResponse.BestDefense bestDefense = repository.findDistinctTeamIds(tournamentId)
                .stream()
                .map(teamId -> new RecognitionResponse.BestDefense(
                        teamId, repository.sumTeamGoalsAgainst(teamId, tournamentId)))
                .min(java.util.Comparator.comparingLong(RecognitionResponse.BestDefense::goalsAgainst))
                .orElse(null);

        return new RecognitionResponse(tournamentId, topScorer, bestDefense);
    }

    /**
     * Calcula todas las estadisticas de un equipo (partidos, puntos, goles)
     * para un torneo puntual. tournamentId puede ser null solo si se quiere
     * historico general (no se usa asi en los endpoints actuales, pero el
     * repositorio ya lo soporta).
     */
    private TeamStatisticsResponse buildTeamStatistics(Long teamId, Long tournamentId) {
        long played = repository.countTeamMatchesPlayed(teamId, tournamentId);
        long wins = repository.countTeamMatchesByResult(teamId, tournamentId, MatchResult.WON);
        long draws = repository.countTeamMatchesByResult(teamId, tournamentId, MatchResult.DRAWN);
        long losses = repository.countTeamMatchesByResult(teamId, tournamentId, MatchResult.LOST);
        long goalsFor = repository.sumTeamGoalsFor(teamId, tournamentId);
        long goalsAgainst = repository.sumTeamGoalsAgainst(teamId, tournamentId);
        long points = (wins * 3) + draws;

        return new TeamStatisticsResponse(
                teamId, tournamentId, played, wins, draws, losses,
                goalsFor, goalsAgainst, goalsFor - goalsAgainst, points);
    }

    private Integer defaultZero(Integer value) {
        return value == null ? 0 : value;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

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

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final PlayerMatchStatRepository repository;
    private final TournamentClient tournamentClient;

    @Override
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
        List<PlayerMatchStat> stats = fetchPlayerStats(playerId, tournamentId);
        long played = stats.size();
        double winRatePercentage = 0.0;
        if (played > 0) {
            long won = stats.stream().filter(s -> s.getResult() == MatchResult.WON).count();
            winRatePercentage = round((won * 100.0) / played);
        }
        return new PlayerAverageResponse(playerId, tournamentId, "averageWinRatePercentage",
                winRatePercentage, played);
    }

    @Override
    public PlayerAverageResponse getAverageGoals(Long playerId, Long tournamentId) {
        List<PlayerMatchStat> stats = fetchPlayerStats(playerId, tournamentId);
        double average = averageOf(stats, PlayerMatchStat::getGoals);
        return new PlayerAverageResponse(playerId, tournamentId, "averageGoals", average, stats.size());
    }

    @Override
    public PlayerAverageResponse getAverageFouls(Long playerId, Long tournamentId) {
        List<PlayerMatchStat> stats = fetchPlayerStats(playerId, tournamentId);
        double average = averageOf(stats, PlayerMatchStat::getFoulsCommitted);
        return new PlayerAverageResponse(playerId, tournamentId, "averageFouls", average, stats.size());
    }

    @Override
    public PlayerAverageResponse getAverageMinutesPlayed(Long playerId, Long tournamentId) {
        List<PlayerMatchStat> stats = fetchPlayerStats(playerId, tournamentId);
        double average = averageOf(stats, PlayerMatchStat::getMinutesPlayed);
        return new PlayerAverageResponse(playerId, tournamentId, "averageMinutesPlayed", average, stats.size());
    }

    @Override
    public MatchesPlayedResponse getMatchesPlayed(Long playerId, Long tournamentId) {
        List<PlayerMatchStat> stats = fetchPlayerStats(playerId, tournamentId);
        return new MatchesPlayedResponse(playerId, tournamentId, stats.size());
    }

    @Override
    public RankingResponse getRanking(RankingType type, Long tournamentId, int limit) {
        List<PlayerMatchStat> stats = tournamentId == null
                ? repository.findAll()
                : repository.findByTournamentId(tournamentId);

        Map<Long, Long> totalsByPlayer = stats.stream()
                .collect(Collectors.groupingBy(
                        PlayerMatchStat::getPlayerId,
                        Collectors.summingLong(stat -> valueForRanking(stat, type))
                ));

        Comparator<Map.Entry<Long, Long>> comparator = type == RankingType.FOULS
                ? Map.Entry.comparingByValue()
                : Map.Entry.<Long, Long>comparingByValue().reversed();

        List<RankingEntryResponse> entries = new java.util.ArrayList<>();
        int position = 1;
        for (Map.Entry<Long, Long> entry : totalsByPlayer.entrySet().stream()
                .sorted(comparator)
                .limit(Math.max(limit, 1))
                .toList()) {
            entries.add(new RankingEntryResponse(position++, entry.getKey(), entry.getValue()));
        }

        return new RankingResponse(type.name(), tournamentId, entries);
    }

    @Override
    public TournamentStandingsResponse getTournamentStandings(Long tournamentId) {
        List<Long> teamIds = repository.findByTournamentId(tournamentId).stream()
                .map(PlayerMatchStat::getTeamId)
                .distinct()
                .toList();

        List<TeamStatisticsResponse> standings = teamIds.stream()
                .map(teamId -> buildTeamStatistics(teamId, tournamentId))
                .sorted(
                        Comparator.comparingLong(TeamStatisticsResponse::points).reversed()
                                .thenComparing(Comparator.comparingLong(
                                        TeamStatisticsResponse::goalDifference).reversed())
                                .thenComparing(Comparator.comparingLong(
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
        List<PlayerMatchStat> tournamentStats = repository.findByTournamentId(tournamentId);

        RecognitionResponse.TopScorer topScorer = tournamentStats.stream()
                .collect(Collectors.groupingBy(PlayerMatchStat::getPlayerId,
                        Collectors.summingInt(PlayerMatchStat::getGoals)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> new RecognitionResponse.TopScorer(e.getKey(), e.getValue()))
                .orElse(null);

        List<Long> teamIds = tournamentStats.stream()
                .map(PlayerMatchStat::getTeamId)
                .distinct()
                .toList();

        RecognitionResponse.BestDefense bestDefense = teamIds.stream()
                .map(teamId -> new RecognitionResponse.BestDefense(
                        teamId, buildTeamStatistics(teamId, tournamentId).goalsAgainst()))
                .min(Comparator.comparingLong(RecognitionResponse.BestDefense::goalsAgainst))
                .orElse(null);

        return new RecognitionResponse(tournamentId, topScorer, bestDefense);
    }

    // ---------- Helpers privados ----------

    private List<PlayerMatchStat> fetchPlayerStats(Long playerId, Long tournamentId) {
        return tournamentId == null
                ? repository.findByPlayerId(playerId)
                : repository.findByPlayerIdAndTournamentId(playerId, tournamentId);
    }

    private List<PlayerMatchStat> fetchTeamStats(Long teamId, Long tournamentId) {
        return tournamentId == null
                ? repository.findByTeamId(teamId)
                : repository.findByTeamIdAndTournamentId(teamId, tournamentId);
    }

    private double averageOf(List<PlayerMatchStat> stats, java.util.function.ToIntFunction<PlayerMatchStat> field) {
        if (stats.isEmpty()) {
            return 0.0;
        }
        return round(stats.stream().mapToInt(field).average().orElse(0.0));
    }

    private long valueForRanking(PlayerMatchStat stat, RankingType type) {
        return switch (type) {
            case GOALS -> stat.getGoals();
            case FOULS -> stat.getFoulsCommitted();
            case MINUTES -> stat.getMinutesPlayed();
            case WINS -> stat.getResult() == MatchResult.WON ? 1 : 0;
        };
    }

    /**
     * Calcula todas las estadisticas de un equipo (partidos, puntos, goles)
     * para un torneo puntual.
     */
    private TeamStatisticsResponse buildTeamStatistics(Long teamId, Long tournamentId) {
        List<PlayerMatchStat> teamStats = fetchTeamStats(teamId, tournamentId);

        // Cada partido que el equipo jugo aparece repetido (una fila por
        // jugador), por eso agrupamos por matchId para no contar de mas.
        Map<Long, MatchResult> resultByMatch = teamStats.stream()
                .collect(Collectors.toMap(
                        PlayerMatchStat::getMatchId,
                        PlayerMatchStat::getResult,
                        (a, b) -> a));

        long played = resultByMatch.size();
        long wins = resultByMatch.values().stream().filter(r -> r == MatchResult.WON).count();
        long draws = resultByMatch.values().stream().filter(r -> r == MatchResult.DRAWN).count();
        long losses = resultByMatch.values().stream().filter(r -> r == MatchResult.LOST).count();

        long goalsFor = teamStats.stream().mapToInt(PlayerMatchStat::getGoals).sum();

        // Goles en contra: para cada partido de este equipo, se suman los
        // goles de los jugadores del equipo RIVAL en ese mismo partido.
        long goalsAgainst = resultByMatch.keySet().stream()
                .mapToLong(matchId -> repository.findByMatchId(matchId).stream()
                        .filter(s -> !s.getTeamId().equals(teamId))
                        .mapToInt(PlayerMatchStat::getGoals)
                        .sum())
                .sum();

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

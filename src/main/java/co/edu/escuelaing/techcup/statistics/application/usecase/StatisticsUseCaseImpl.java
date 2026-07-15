package co.edu.escuelaing.techcup.statistics.application.usecase;

import co.edu.escuelaing.techcup.statistics.domain.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.domain.exception.RecognitionNotFoundException;
import co.edu.escuelaing.techcup.statistics.domain.model.*;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.in.StatisticsUseCase;
import co.edu.escuelaing.techcup.statistics.domain.validator.MatchStatEventValidator;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.feign.TournamentClient;
import co.edu.escuelaing.techcup.statistics.application.mapper.PlayerMatchStatMapper;
import co.edu.escuelaing.techcup.statistics.application.mapper.TournamentRecognitionMapper;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.PlayerMatchStatDocument;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.TournamentRecognitionDocument;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.PlayerMatchStatRepository;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.TournamentRecognitionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsUseCaseImpl implements StatisticsUseCase {

    private final PlayerMatchStatRepository repository;
    private final TournamentRecognitionRepository recognitionRepository;
    private final TournamentClient tournamentClient;
    private final PlayerMatchStatMapper playerMatchStatMapper;
    private final TournamentRecognitionMapper recognitionMapper;

    @Override
    public void registerMatchStat(PlayerMatchStatistic statistic) {
        MatchStatEventValidator.validate(statistic);

        if (repository.existsByPlayerIdAndMatchId(statistic.getPlayerId(), statistic.getMatchId())) {
            throw new DuplicateMatchStatException(statistic.getPlayerId(), statistic.getMatchId());
        }

        PlayerMatchStatistic toSave = statistic.toBuilder()
                .registeredAt(LocalDateTime.now())
                .build();

        repository.save(playerMatchStatMapper.toDocument(toSave));
    }

    @Override
    public PlayerAverageResult getAverageWinRate(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        long played = stats.size();
        double winRatePercentage = 0.0;
        if (played > 0) {
            long won = stats.stream().filter(s -> s.getResult() == MatchResult.WON).count();
            winRatePercentage = round((won * 100.0) / played);
        }
        return new PlayerAverageResult(playerId, tournamentId, "averageWinRatePercentage", winRatePercentage, played);
    }

    @Override
    public PlayerAverageResult getAverageGoals(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        double average = averageOf(stats, PlayerMatchStatistic::getGoals);
        return new PlayerAverageResult(playerId, tournamentId, "averageGoals", average, stats.size());
    }

    @Override
    public PlayerAverageResult getAverageFouls(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        double average = averageOf(stats, PlayerMatchStatistic::getFoulsCommitted);
        return new PlayerAverageResult(playerId, tournamentId, "averageFouls", average, stats.size());
    }

    @Override
    public PlayerAverageResult getAverageMinutesPlayed(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        double average = averageOf(stats, PlayerMatchStatistic::getMinutesPlayed);
        return new PlayerAverageResult(playerId, tournamentId, "averageMinutesPlayed", average, stats.size());
    }

    @Override
    public MatchesPlayedResult getMatchesPlayed(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        return new MatchesPlayedResult(playerId, tournamentId, stats.size());
    }

    @Override
    public RankingResult getRanking(RankingType type, String tournamentId, int limit) {
        List<PlayerMatchStatistic> stats = fetchTournamentStats(tournamentId);

        Map<String, Long> totalsByPlayer = stats.stream()
                .collect(Collectors.groupingBy(
                        PlayerMatchStatistic::getPlayerId,
                        Collectors.summingLong(stat -> valueForRanking(stat, type))));

        Comparator<Map.Entry<String, Long>> comparator = type == RankingType.FOULS
                ? Map.Entry.comparingByValue()
                : Map.Entry.<String, Long>comparingByValue().reversed();

        List<RankingResult.RankingEntry> entries = new java.util.ArrayList<>();
        int position = 1;
        for (Map.Entry<String, Long> entry : totalsByPlayer.entrySet().stream()
                .sorted(comparator).limit(Math.max(limit, 1)).toList()) {
            entries.add(new RankingResult.RankingEntry(position++, entry.getKey(), entry.getValue()));
        }

        return new RankingResult(type.name(), tournamentId, entries);
    }

    @Override
    public TournamentStandingsResult getTournamentStandings(String tournamentId) {
        List<String> teamIds = fetchTournamentStats(tournamentId).stream()
                .map(PlayerMatchStatistic::getTeamId).distinct().toList();

        List<TeamStatisticsResult> standings = teamIds.stream()
                .map(teamId -> buildTeamStatistics(teamId, tournamentId))
                .sorted(Comparator.comparingLong(TeamStatisticsResult::points).reversed()
                        .thenComparing(Comparator.comparingLong(TeamStatisticsResult::goalDifference).reversed())
                        .thenComparing(Comparator.comparingLong(TeamStatisticsResult::goalsFor).reversed()))
                .toList();

        return new TournamentStandingsResult(tournamentId, standings);
    }

    @Override
    public TeamStatisticsResult getTeamStatistics(String teamId, String tournamentId) {
        String resolvedId = tournamentId;
        if (resolvedId == null) {
            resolvedId = tournamentClient.getActiveTournamentId();
        }
        return buildTeamStatistics(teamId, resolvedId);
    }

    @Override
    public TournamentRecognitionRecord generateTournamentRecognitions(String tournamentId) {
        List<PlayerMatchStatistic> tournamentStats = fetchTournamentStats(tournamentId);

        Map<String, Integer> goalsByPlayer = tournamentStats.stream()
                .collect(Collectors.groupingBy(PlayerMatchStatistic::getPlayerId,
                        Collectors.summingInt(PlayerMatchStatistic::getGoals)));

        long maxGoals = goalsByPlayer.values().stream().mapToLong(Integer::longValue).max().orElse(0);
        List<String> topScorerIds = goalsByPlayer.entrySet().stream()
                .filter(e -> e.getValue() == maxGoals && maxGoals > 0)
                .map(Map.Entry::getKey).toList();

        List<String> teamIds = tournamentStats.stream()
                .map(PlayerMatchStatistic::getTeamId).distinct().toList();

        Map<String, Long> goalsAgainstByTeam = teamIds.stream()
                .collect(Collectors.toMap(id -> id, id -> buildTeamStatistics(id, tournamentId).goalsAgainst()));

        long minGoalsAgainst = goalsAgainstByTeam.values().stream().mapToLong(Long::longValue).min().orElse(0);
        List<String> bestDefenseIds = goalsAgainstByTeam.entrySet().stream()
                .filter(e -> e.getValue() == minGoalsAgainst).map(Map.Entry::getKey).toList();

        Optional<TournamentRecognitionDocument> existing = recognitionRepository.findByTournamentId(tournamentId);

        TournamentRecognitionRecord domain = TournamentRecognitionRecord.builder()
                .tournamentId(tournamentId).topScorerPlayerIds(topScorerIds)
                .topScorersGoals(maxGoals).bestDefenseTeamIds(bestDefenseIds)
                .bestDefenseGoalsAgainst(minGoalsAgainst).generatedAt(LocalDateTime.now()).build();

        TournamentRecognitionDocument doc = recognitionMapper.toDocument(domain);
        existing.ifPresent(e -> doc.setId(e.getId()));
        recognitionRepository.save(doc);
        return domain;
    }

    @Override
    public TournamentRecognitionRecord getTournamentRecognitions(String tournamentId) {
        return recognitionRepository.findByTournamentId(tournamentId)
                .map(recognitionMapper::toDomain)
                .orElseThrow(() -> new RecognitionNotFoundException(tournamentId));
    }

    @Override
    public GoalkeeperRankingResult getGoalkeeperRanking(String tournamentId, int limit) {
        List<PlayerMatchStatistic> stats = fetchTournamentStats(tournamentId);
        Map<String, List<PlayerMatchStatistic>> byMatch = stats.stream()
                .collect(Collectors.groupingBy(PlayerMatchStatistic::getMatchId));

        Map<String, Long> conceded = new java.util.HashMap<>();
        for (PlayerMatchStatistic stat : stats) {
            if (!stat.isGoalkeeper()) continue;
            long opponentGoals = byMatch.getOrDefault(stat.getMatchId(), List.of()).stream()
                    .filter(row -> !row.getTeamId().equals(stat.getTeamId()))
                    .mapToInt(PlayerMatchStatistic::getGoals).sum();
            conceded.merge(stat.getPlayerId(), opponentGoals, Long::sum);
        }

        List<GoalkeeperRankingResult.GoalkeeperEntry> entries = new java.util.ArrayList<>();
        int pos = 1;
        for (Map.Entry<String, Long> e : conceded.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()).limit(Math.max(limit, 1)).toList()) {
            entries.add(new GoalkeeperRankingResult.GoalkeeperEntry(pos++, e.getKey(), e.getValue()));
        }
        return new GoalkeeperRankingResult(tournamentId, entries);
    }

    @Override public TotalResult getPlayerTotalAssists(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        return new TotalResult(playerId, tournamentId, "totalAssists",
                stats.stream().mapToInt(PlayerMatchStatistic::getAssists).sum(), stats.size());
    }

    @Override public TotalResult getPlayerTotalGoals(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        return new TotalResult(playerId, tournamentId, "totalGoals",
                stats.stream().mapToInt(PlayerMatchStatistic::getGoals).sum(), stats.size());
    }

    @Override public TotalResult getPlayerTotalFouls(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        return new TotalResult(playerId, tournamentId, "totalFouls",
                stats.stream().mapToInt(PlayerMatchStatistic::getFoulsCommitted).sum(), stats.size());
    }

    @Override public PlayerCardsResult getPlayerCards(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        return new PlayerCardsResult(playerId, tournamentId,
                stats.stream().mapToInt(PlayerMatchStatistic::getYellowCards).sum(),
                stats.stream().mapToInt(PlayerMatchStatistic::getRedCards).sum());
    }

    @Override public TeamMatchRecordResult getTeamMatchRecord(String teamId, String tournamentId) {
        var resultByMatch = resultByMatchForTeam(teamId, tournamentId);
        long played = resultByMatch.size();
        long wins = resultByMatch.values().stream().filter(r -> r == MatchResult.WON).count();
        long draws = resultByMatch.values().stream().filter(r -> r == MatchResult.DRAWN).count();
        long losses = resultByMatch.values().stream().filter(r -> r == MatchResult.LOST).count();
        double wr = played == 0 ? 0 : round((wins * 100.0) / played);
        double dr = played == 0 ? 0 : round((draws * 100.0) / played);
        double lr = played == 0 ? 0 : round((losses * 100.0) / played);
        return new TeamMatchRecordResult(teamId, tournamentId, played, wins, draws, losses, wr, dr, lr);
    }

    @Override public TeamAverageResult getTeamAverageGoals(String teamId, String tournamentId) {
        var teamStats = fetchTeamStats(teamId, tournamentId);
        long matches = teamStats.stream().map(PlayerMatchStatistic::getMatchId).distinct().count();
        double avg = matches == 0 ? 0 : round((double) teamStats.stream().mapToInt(PlayerMatchStatistic::getGoals).sum() / matches);
        return new TeamAverageResult(teamId, tournamentId, "averageGoalsPerMatch", avg, matches);
    }

    @Override public TeamAverageResult getTeamAverageFouls(String teamId, String tournamentId) {
        var teamStats = fetchTeamStats(teamId, tournamentId);
        long matches = teamStats.stream().map(PlayerMatchStatistic::getMatchId).distinct().count();
        double avg = matches == 0 ? 0 : round((double) teamStats.stream().mapToInt(PlayerMatchStatistic::getFoulsCommitted).sum() / matches);
        return new TeamAverageResult(teamId, tournamentId, "averageFoulsPerMatch", avg, matches);
    }

    @Override public TotalResult getTeamTotalFouls(String teamId, String tournamentId) {
        var teamStats = fetchTeamStats(teamId, tournamentId);
        long matches = teamStats.stream().map(PlayerMatchStatistic::getMatchId).distinct().count();
        return new TotalResult(teamId, tournamentId, "totalFouls",
                teamStats.stream().mapToInt(PlayerMatchStatistic::getFoulsCommitted).sum(), matches);
    }

    @Override public TeamGoalsResult getTeamGoals(String teamId, String tournamentId) {
        var s = buildTeamStatistics(teamId, tournamentId);
        return new TeamGoalsResult(teamId, tournamentId, s.goalsFor(), s.goalsAgainst(), s.goalDifference());
    }

    @Override public TournamentMatchAveragesResult getTournamentMatchAverages(String tournamentId) {
        var stats = fetchTournamentStats(tournamentId);
        long matches = stats.stream().map(PlayerMatchStatistic::getMatchId).distinct().count();
        if (matches == 0) return new TournamentMatchAveragesResult(tournamentId, 0, 0, 0, 0);
        long g = stats.stream().mapToInt(PlayerMatchStatistic::getGoals).sum();
        long f = stats.stream().mapToInt(PlayerMatchStatistic::getFoulsCommitted).sum();
        long c = stats.stream().mapToInt(s -> s.getYellowCards() + s.getRedCards()).sum();
        return new TournamentMatchAveragesResult(tournamentId, matches, round((double) g / matches),
                round((double) f / matches), round((double) c / matches));
    }

    @Override public CardsTotalResult getTournamentCardsTotal(String tournamentId) {
        var stats = fetchTournamentStats(tournamentId);
        return new CardsTotalResult("tournament", tournamentId,
                stats.stream().mapToInt(PlayerMatchStatistic::getYellowCards).sum(),
                stats.stream().mapToInt(PlayerMatchStatistic::getRedCards).sum());
    }

    @Override public CardsTotalResult getMatchCardsTotal(String matchId) {
        var stats = fetchMatchStats(matchId);
        return new CardsTotalResult("match", matchId,
                stats.stream().mapToInt(PlayerMatchStatistic::getYellowCards).sum(),
                stats.stream().mapToInt(PlayerMatchStatistic::getRedCards).sum());
    }

    @Override public MatchResultResult getMatchResult(String matchId) {
        var stats = fetchMatchStats(matchId);
        String tid = stats.stream().map(PlayerMatchStatistic::getTournamentId).findFirst().orElse(null);
        var teamResults = stats.stream()
                .collect(Collectors.toMap(PlayerMatchStatistic::getTeamId, PlayerMatchStatistic::getResult, (a, b) -> a))
                .entrySet().stream()
                .map(e -> new MatchResultResult.TeamResultEntry(e.getKey(), e.getValue())).toList();
        return new MatchResultResult(matchId, tid, teamResults);
    }

    // --- Helpers ---

    private List<PlayerMatchStatistic> fetchPlayerStats(String playerId, String tournamentId) {
        List<PlayerMatchStatDocument> docs = tournamentId == null
                ? repository.findByPlayerId(playerId)
                : repository.findByPlayerIdAndTournamentId(playerId, tournamentId);
        return playerMatchStatMapper.toDomainList(docs);
    }

    private List<PlayerMatchStatistic> fetchTeamStats(String teamId, String tournamentId) {
        List<PlayerMatchStatDocument> docs = tournamentId == null
                ? repository.findByTeamId(teamId)
                : repository.findByTeamIdAndTournamentId(teamId, tournamentId);
        return playerMatchStatMapper.toDomainList(docs);
    }

    private List<PlayerMatchStatistic> fetchTournamentStats(String tournamentId) {
        List<PlayerMatchStatDocument> docs = tournamentId == null
                ? repository.findAll()
                : repository.findByTournamentId(tournamentId);
        return playerMatchStatMapper.toDomainList(docs);
    }

    private List<PlayerMatchStatistic> fetchMatchStats(String matchId) {
        return playerMatchStatMapper.toDomainList(repository.findByMatchId(matchId));
    }

    private double averageOf(List<PlayerMatchStatistic> stats, java.util.function.ToIntFunction<PlayerMatchStatistic> fn) {
        return stats.isEmpty() ? 0.0 : round(stats.stream().mapToInt(fn).average().orElse(0.0));
    }

    private long valueForRanking(PlayerMatchStatistic stat, RankingType type) {
        return switch (type) {
            case GOALS -> stat.getGoals();
            case FOULS -> stat.getFoulsCommitted();
            case MINUTES -> stat.getMinutesPlayed();
            case WINS -> stat.getResult() == MatchResult.WON ? 1 : 0;
        };
    }

    private Map<String, MatchResult> resultByMatchForTeam(String teamId, String tournamentId) {
        return fetchTeamStats(teamId, tournamentId).stream()
                .collect(Collectors.toMap(PlayerMatchStatistic::getMatchId, PlayerMatchStatistic::getResult, (a, b) -> a));
    }

    private TeamStatisticsResult buildTeamStatistics(String teamId, String tournamentId) {
        var teamStats = fetchTeamStats(teamId, tournamentId);
        var resultByMatch = resultByMatchForTeam(teamId, tournamentId);
        long played = resultByMatch.size();
        long wins = resultByMatch.values().stream().filter(r -> r == MatchResult.WON).count();
        long draws = resultByMatch.values().stream().filter(r -> r == MatchResult.DRAWN).count();
        long losses = resultByMatch.values().stream().filter(r -> r == MatchResult.LOST).count();
        long gf = teamStats.stream().mapToInt(PlayerMatchStatistic::getGoals).sum();
        long ga = resultByMatch.keySet().stream()
                .mapToLong(mid -> fetchMatchStats(mid).stream()
                        .filter(s -> !s.getTeamId().equals(teamId))
                        .mapToInt(PlayerMatchStatistic::getGoals).sum()).sum();
        return new TeamStatisticsResult(teamId, tournamentId, played, wins, draws, losses, gf, ga, gf - ga, (wins * 3) + draws);
    }

    private double round(double v) { return Math.round(v * 100.0) / 100.0; }
}

package co.edu.escuelaing.techcup.statistics.application.usecase;

import co.edu.escuelaing.techcup.statistics.domain.model.CardsTotalResult;
import co.edu.escuelaing.techcup.statistics.domain.model.GoalkeeperRankingResult;
import co.edu.escuelaing.techcup.statistics.domain.model.MatchResult;
import co.edu.escuelaing.techcup.statistics.domain.model.MatchResultResult;
import co.edu.escuelaing.techcup.statistics.domain.model.MatchesPlayedResult;
import co.edu.escuelaing.techcup.statistics.domain.model.PlayerAverageResult;
import co.edu.escuelaing.techcup.statistics.domain.model.PlayerCardsResult;
import co.edu.escuelaing.techcup.statistics.domain.model.PlayerMatchStatistic;
import co.edu.escuelaing.techcup.statistics.domain.model.RankingResult;
import co.edu.escuelaing.techcup.statistics.domain.model.RankingType;
import co.edu.escuelaing.techcup.statistics.domain.model.TeamAverageResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TeamGoalsResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TeamMatchRecordResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TeamStatisticsResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TotalResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TournamentMatchAveragesResult;
import co.edu.escuelaing.techcup.statistics.domain.model.TournamentRecognitionRecord;
import co.edu.escuelaing.techcup.statistics.domain.model.TournamentStandingsResult;
import co.edu.escuelaing.techcup.statistics.domain.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.domain.exception.RecognitionNotFoundException;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.in.StatisticsUseCase;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.out.PlayerMatchStatRepositoryPort;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.out.TournamentClientPort;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.out.TournamentRecognitionRepositoryPort;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

/**
 * Implementacion del caso de uso. SOLO depende de los 3 puertos de salida
 * (interfaces de dominio) -- nunca de Spring Data, MongoDB, MapStruct, ni de
 * ningun DTO web. Esta clase se podria testear, o incluso reusar, sin
 * Spring, sin Mongo y sin HTTP.
 */
@Service
@RequiredArgsConstructor
public class StatisticsUseCaseImpl implements StatisticsUseCase {

    private final PlayerMatchStatRepositoryPort playerMatchStatRepository;
    private final TournamentRecognitionRepositoryPort recognitionRepository;
    private final TournamentClientPort tournamentClient;

    @Override
    public void registerMatchStat(PlayerMatchStatistic statistic) {
        if (playerMatchStatRepository.existsByPlayerIdAndMatchId(statistic.getPlayerId(), statistic.getMatchId())) {
            throw new DuplicateMatchStatException(statistic.getPlayerId(), statistic.getMatchId());
        }

        PlayerMatchStatistic toSave = statistic.toBuilder()
                .registeredAt(LocalDateTime.now())
                .build();

        playerMatchStatRepository.save(toSave);
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
        return new PlayerAverageResult(playerId, tournamentId, "averageWinRatePercentage",
                winRatePercentage, played);
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
                        Collectors.summingLong(stat -> valueForRanking(stat, type))
                ));

        Comparator<Map.Entry<String, Long>> comparator = type == RankingType.FOULS
                ? Map.Entry.comparingByValue()
                : Map.Entry.<String, Long>comparingByValue().reversed();

        List<RankingResult.RankingEntry> entries = new java.util.ArrayList<>();
        int position = 1;
        for (Map.Entry<String, Long> entry : totalsByPlayer.entrySet().stream()
                .sorted(comparator)
                .limit(Math.max(limit, 1))
                .toList()) {
            entries.add(new RankingResult.RankingEntry(position++, entry.getKey(), entry.getValue()));
        }

        return new RankingResult(type.name(), tournamentId, entries);
    }

    @Override
    public TournamentStandingsResult getTournamentStandings(String tournamentId) {
        List<String> teamIds = fetchTournamentStats(tournamentId).stream()
                .map(PlayerMatchStatistic::getTeamId)
                .distinct()
                .toList();

        List<TeamStatisticsResult> standings = teamIds.stream()
                .map(teamId -> buildTeamStatistics(teamId, tournamentId))
                .sorted(
                        Comparator.comparingLong(TeamStatisticsResult::points).reversed()
                                .thenComparing(Comparator.comparingLong(
                                        TeamStatisticsResult::goalDifference).reversed())
                                .thenComparing(Comparator.comparingLong(
                                        TeamStatisticsResult::goalsFor).reversed())
                )
                .toList();

        return new TournamentStandingsResult(tournamentId, standings);
    }

    @Override
    public TeamStatisticsResult getTeamStatisticsInActiveTournament(String teamId) {
        String activeTournamentId = tournamentClient.getActiveTournamentId();
        return buildTeamStatistics(teamId, activeTournamentId);
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
                .map(Map.Entry::getKey)
                .toList();

        List<String> teamIds = tournamentStats.stream()
                .map(PlayerMatchStatistic::getTeamId)
                .distinct()
                .toList();

        Map<String, Long> goalsAgainstByTeam = teamIds.stream()
                .collect(Collectors.toMap(id -> id, id -> buildTeamStatistics(id, tournamentId).goalsAgainst()));

        long minGoalsAgainst = goalsAgainstByTeam.values().stream()
                .mapToLong(Long::longValue).min().orElse(0);
        List<String> bestDefenseIds = goalsAgainstByTeam.entrySet().stream()
                .filter(e -> e.getValue() == minGoalsAgainst)
                .map(Map.Entry::getKey)
                .toList();

        TournamentRecognitionRecord toSave = TournamentRecognitionRecord.builder()
                .tournamentId(tournamentId)
                .topScorerPlayerIds(topScorerIds)
                .topScorersGoals(maxGoals)
                .bestDefenseTeamIds(bestDefenseIds)
                .bestDefenseGoalsAgainst(minGoalsAgainst)
                .generatedAt(LocalDateTime.now())
                .build();

        // El adaptador decide si esto es un insert o un reemplazo -- la
        // aplicacion no necesita saber nada de ids de Mongo.
        return recognitionRepository.save(toSave);
    }

    @Override
    public TournamentRecognitionRecord getTournamentRecognitions(String tournamentId) {
        return recognitionRepository.findByTournamentId(tournamentId)
                .orElseThrow(() -> new RecognitionNotFoundException(tournamentId));
    }

    @Override
    public GoalkeeperRankingResult getGoalkeeperRanking(String tournamentId, int limit) {
        List<PlayerMatchStatistic> stats = fetchTournamentStats(tournamentId);

        Map<String, List<PlayerMatchStatistic>> byMatch = stats.stream()
                .collect(Collectors.groupingBy(PlayerMatchStatistic::getMatchId));

        Map<String, Long> goalsConcededByGoalkeeper = new java.util.HashMap<>();
        for (PlayerMatchStatistic stat : stats) {
            if (!stat.isGoalkeeper()) {
                continue;
            }
            long opponentGoals = byMatch.getOrDefault(stat.getMatchId(), List.of()).stream()
                    .filter(row -> !row.getTeamId().equals(stat.getTeamId()))
                    .mapToInt(PlayerMatchStatistic::getGoals)
                    .sum();
            goalsConcededByGoalkeeper.merge(stat.getPlayerId(), opponentGoals, Long::sum);
        }

        List<GoalkeeperRankingResult.GoalkeeperEntry> entries = new java.util.ArrayList<>();
        int position = 1;
        for (Map.Entry<String, Long> entry : goalsConcededByGoalkeeper.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(Math.max(limit, 1))
                .toList()) {
            entries.add(new GoalkeeperRankingResult.GoalkeeperEntry(position++, entry.getKey(), entry.getValue()));
        }

        return new GoalkeeperRankingResult(tournamentId, entries);
    }

    @Override
    public TotalResult getPlayerTotalAssists(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        long total = stats.stream().mapToInt(PlayerMatchStatistic::getAssists).sum();
        return new TotalResult(playerId, tournamentId, "totalAssists", total, stats.size());
    }

    @Override
    public TotalResult getPlayerTotalGoals(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        long total = stats.stream().mapToInt(PlayerMatchStatistic::getGoals).sum();
        return new TotalResult(playerId, tournamentId, "totalGoals", total, stats.size());
    }

    @Override
    public TotalResult getPlayerTotalFouls(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        long total = stats.stream().mapToInt(PlayerMatchStatistic::getFoulsCommitted).sum();
        return new TotalResult(playerId, tournamentId, "totalFouls", total, stats.size());
    }

    @Override
    public PlayerCardsResult getPlayerCards(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        long yellow = stats.stream().mapToInt(PlayerMatchStatistic::getYellowCards).sum();
        long red = stats.stream().mapToInt(PlayerMatchStatistic::getRedCards).sum();
        return new PlayerCardsResult(playerId, tournamentId, yellow, red);
    }

    @Override
    public TeamMatchRecordResult getTeamMatchRecord(String teamId, String tournamentId) {
        Map<String, MatchResult> resultByMatch = resultByMatchForTeam(teamId, tournamentId);
        long played = resultByMatch.size();
        long wins = resultByMatch.values().stream().filter(r -> r == MatchResult.WON).count();
        long draws = resultByMatch.values().stream().filter(r -> r == MatchResult.DRAWN).count();
        long losses = resultByMatch.values().stream().filter(r -> r == MatchResult.LOST).count();

        double winRate = played == 0 ? 0.0 : round((wins * 100.0) / played);
        double drawRate = played == 0 ? 0.0 : round((draws * 100.0) / played);
        double lossRate = played == 0 ? 0.0 : round((losses * 100.0) / played);

        return new TeamMatchRecordResult(teamId, tournamentId, played, wins, draws, losses,
                winRate, drawRate, lossRate);
    }

    @Override
    public TeamAverageResult getTeamAverageGoals(String teamId, String tournamentId) {
        List<PlayerMatchStatistic> teamStats = fetchTeamStats(teamId, tournamentId);
        long matchesPlayed = teamStats.stream().map(PlayerMatchStatistic::getMatchId).distinct().count();
        long totalGoals = teamStats.stream().mapToInt(PlayerMatchStatistic::getGoals).sum();
        double average = matchesPlayed == 0 ? 0.0 : round((double) totalGoals / matchesPlayed);
        return new TeamAverageResult(teamId, tournamentId, "averageGoalsPerMatch", average, matchesPlayed);
    }

    @Override
    public TeamAverageResult getTeamAverageFouls(String teamId, String tournamentId) {
        List<PlayerMatchStatistic> teamStats = fetchTeamStats(teamId, tournamentId);
        long matchesPlayed = teamStats.stream().map(PlayerMatchStatistic::getMatchId).distinct().count();
        long totalFouls = teamStats.stream().mapToInt(PlayerMatchStatistic::getFoulsCommitted).sum();
        double average = matchesPlayed == 0 ? 0.0 : round((double) totalFouls / matchesPlayed);
        return new TeamAverageResult(teamId, tournamentId, "averageFoulsPerMatch", average, matchesPlayed);
    }

    @Override
    public TotalResult getTeamTotalFouls(String teamId, String tournamentId) {
        List<PlayerMatchStatistic> teamStats = fetchTeamStats(teamId, tournamentId);
        long matchesPlayed = teamStats.stream().map(PlayerMatchStatistic::getMatchId).distinct().count();
        long totalFouls = teamStats.stream().mapToInt(PlayerMatchStatistic::getFoulsCommitted).sum();
        return new TotalResult(teamId, tournamentId, "totalFouls", totalFouls, matchesPlayed);
    }

    @Override
    public TeamGoalsResult getTeamGoals(String teamId, String tournamentId) {
        TeamStatisticsResult stats = buildTeamStatistics(teamId, tournamentId);
        return new TeamGoalsResult(teamId, tournamentId, stats.goalsFor(), stats.goalsAgainst(),
                stats.goalDifference());
    }

    @Override
    public TournamentMatchAveragesResult getTournamentMatchAverages(String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchTournamentStats(tournamentId);
        long matchesPlayed = stats.stream().map(PlayerMatchStatistic::getMatchId).distinct().count();

        if (matchesPlayed == 0) {
            return new TournamentMatchAveragesResult(tournamentId, 0, 0.0, 0.0, 0.0);
        }

        long totalGoals = stats.stream().mapToInt(PlayerMatchStatistic::getGoals).sum();
        long totalFouls = stats.stream().mapToInt(PlayerMatchStatistic::getFoulsCommitted).sum();
        long totalCards = stats.stream()
                .mapToInt(s -> s.getYellowCards() + s.getRedCards())
                .sum();

        return new TournamentMatchAveragesResult(
                tournamentId,
                matchesPlayed,
                round((double) totalGoals / matchesPlayed),
                round((double) totalFouls / matchesPlayed),
                round((double) totalCards / matchesPlayed));
    }

    @Override
    public CardsTotalResult getTournamentCardsTotal(String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchTournamentStats(tournamentId);
        long yellow = stats.stream().mapToInt(PlayerMatchStatistic::getYellowCards).sum();
        long red = stats.stream().mapToInt(PlayerMatchStatistic::getRedCards).sum();
        return new CardsTotalResult("tournament", tournamentId, yellow, red);
    }

    @Override
    public CardsTotalResult getMatchCardsTotal(String matchId) {
        List<PlayerMatchStatistic> stats = fetchMatchStats(matchId);
        long yellow = stats.stream().mapToInt(PlayerMatchStatistic::getYellowCards).sum();
        long red = stats.stream().mapToInt(PlayerMatchStatistic::getRedCards).sum();
        return new CardsTotalResult("match", matchId, yellow, red);
    }

    @Override
    public MatchResultResult getMatchResult(String matchId) {
        List<PlayerMatchStatistic> stats = fetchMatchStats(matchId);

        String tournamentId = stats.stream()
                .map(PlayerMatchStatistic::getTournamentId)
                .findFirst()
                .orElse(null);

        List<MatchResultResult.TeamResultEntry> teamResults = stats.stream()
                .collect(Collectors.toMap(
                        PlayerMatchStatistic::getTeamId,
                        PlayerMatchStatistic::getResult,
                        (a, b) -> a))
                .entrySet().stream()
                .map(e -> new MatchResultResult.TeamResultEntry(e.getKey(), e.getValue()))
                .toList();

        return new MatchResultResult(matchId, tournamentId, teamResults);
    }

    // ---------- Helpers privados: acceso a datos via el puerto (sin Mongo, sin mapper) ----------

    private List<PlayerMatchStatistic> fetchPlayerStats(String playerId, String tournamentId) {
        return tournamentId == null
                ? playerMatchStatRepository.findByPlayerId(playerId)
                : playerMatchStatRepository.findByPlayerIdAndTournamentId(playerId, tournamentId);
    }

    private List<PlayerMatchStatistic> fetchTeamStats(String teamId, String tournamentId) {
        return tournamentId == null
                ? playerMatchStatRepository.findByTeamId(teamId)
                : playerMatchStatRepository.findByTeamIdAndTournamentId(teamId, tournamentId);
    }

    private List<PlayerMatchStatistic> fetchTournamentStats(String tournamentId) {
        return tournamentId == null
                ? playerMatchStatRepository.findAll()
                : playerMatchStatRepository.findByTournamentId(tournamentId);
    }

    private List<PlayerMatchStatistic> fetchMatchStats(String matchId) {
        return playerMatchStatRepository.findByMatchId(matchId);
    }

    // ---------- Helpers privados: logica de negocio ----------

    private double averageOf(List<PlayerMatchStatistic> stats,
                              java.util.function.ToIntFunction<PlayerMatchStatistic> field) {
        if (stats.isEmpty()) {
            return 0.0;
        }
        return round(stats.stream().mapToInt(field).average().orElse(0.0));
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
                .collect(Collectors.toMap(
                        PlayerMatchStatistic::getMatchId,
                        PlayerMatchStatistic::getResult,
                        (a, b) -> a));
    }

    private TeamStatisticsResult buildTeamStatistics(String teamId, String tournamentId) {
        List<PlayerMatchStatistic> teamStats = fetchTeamStats(teamId, tournamentId);
        Map<String, MatchResult> resultByMatch = resultByMatchForTeam(teamId, tournamentId);

        long played = resultByMatch.size();
        long wins = resultByMatch.values().stream().filter(r -> r == MatchResult.WON).count();
        long draws = resultByMatch.values().stream().filter(r -> r == MatchResult.DRAWN).count();
        long losses = resultByMatch.values().stream().filter(r -> r == MatchResult.LOST).count();

        long goalsFor = teamStats.stream().mapToInt(PlayerMatchStatistic::getGoals).sum();

        long goalsAgainst = resultByMatch.keySet().stream()
                .mapToLong(matchId -> fetchMatchStats(matchId).stream()
                        .filter(s -> !s.getTeamId().equals(teamId))
                        .mapToInt(PlayerMatchStatistic::getGoals)
                        .sum())
                .sum();

        long points = (wins * 3) + draws;

        return new TeamStatisticsResult(
                teamId, tournamentId, played, wins, draws, losses,
                goalsFor, goalsAgainst, goalsFor - goalsAgainst, points);
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

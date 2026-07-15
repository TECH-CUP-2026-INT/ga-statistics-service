package co.edu.escuelaing.techcup.statistics.service;

import co.edu.escuelaing.techcup.statistics.client.TournamentClient;
import co.edu.escuelaing.techcup.statistics.domain.MatchResult;
import co.edu.escuelaing.techcup.statistics.domain.PlayerMatchStatistic;
import co.edu.escuelaing.techcup.statistics.domain.TournamentRecognitionRecord;
import co.edu.escuelaing.techcup.statistics.dto.CardsTotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.GoalkeeperRankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchResultResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerCardsResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingEntryResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingType;
import co.edu.escuelaing.techcup.statistics.dto.TeamAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamGoalsResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamMatchRecordResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamStatisticsResponse;
import co.edu.escuelaing.techcup.statistics.dto.TotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentMatchAveragesResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentRecognitionResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentStandingsResponse;
import co.edu.escuelaing.techcup.statistics.entity.PlayerMatchStat;
import co.edu.escuelaing.techcup.statistics.entity.TournamentRecognition;
import co.edu.escuelaing.techcup.statistics.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.exception.RecognitionNotFoundException;
import co.edu.escuelaing.techcup.statistics.mapper.PlayerMatchStatMapper;
import co.edu.escuelaing.techcup.statistics.mapper.TournamentRecognitionMapper;
import co.edu.escuelaing.techcup.statistics.repository.PlayerMatchStatRepository;
import co.edu.escuelaing.techcup.statistics.repository.TournamentRecognitionRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

/**
 * IMPORTANTE (separacion de capas): esta clase (el "core" de negocio) SOLO
 * trabaja con objetos de dominio (PlayerMatchStatistic,
 * TournamentRecognitionRecord). Nunca importa ni manipula directamente:
 * - DTOs de entrada de la capa web (MatchStatEventRequest)
 * - Documentos de persistencia (PlayerMatchStat, TournamentRecognition)
 * Esa conversion la hacen los mappers de MapStruct (PlayerMatchStatMapper,
 * TournamentRecognitionMapper), inmediatamente despues de leer del
 * repositorio o justo antes de guardar.
 */
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final PlayerMatchStatRepository repository;
    private final TournamentRecognitionRepository recognitionRepository;
    private final TournamentClient tournamentClient;
    private final PlayerMatchStatMapper playerMatchStatMapper;
    private final TournamentRecognitionMapper recognitionMapper;

    @Override
    public void registerMatchStat(PlayerMatchStatistic statistic) {
        if (repository.existsByPlayerIdAndMatchId(statistic.getPlayerId(), statistic.getMatchId())) {
            throw new DuplicateMatchStatException(statistic.getPlayerId(), statistic.getMatchId());
        }

        PlayerMatchStatistic toSave = statistic.toBuilder()
                .registeredAt(LocalDateTime.now())
                .build();

        repository.save(playerMatchStatMapper.toDocument(toSave));
    }

    @Override
    public PlayerAverageResponse getAverageWinRate(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
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
    public PlayerAverageResponse getAverageGoals(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        double average = averageOf(stats, PlayerMatchStatistic::getGoals);
        return new PlayerAverageResponse(playerId, tournamentId, "averageGoals", average, stats.size());
    }

    @Override
    public PlayerAverageResponse getAverageFouls(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        double average = averageOf(stats, PlayerMatchStatistic::getFoulsCommitted);
        return new PlayerAverageResponse(playerId, tournamentId, "averageFouls", average, stats.size());
    }

    @Override
    public PlayerAverageResponse getAverageMinutesPlayed(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        double average = averageOf(stats, PlayerMatchStatistic::getMinutesPlayed);
        return new PlayerAverageResponse(playerId, tournamentId, "averageMinutesPlayed", average, stats.size());
    }

    @Override
    public MatchesPlayedResponse getMatchesPlayed(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        return new MatchesPlayedResponse(playerId, tournamentId, stats.size());
    }

    @Override
    public RankingResponse getRanking(RankingType type, String tournamentId, int limit) {
        List<PlayerMatchStatistic> stats = fetchTournamentStats(tournamentId);

        Map<String, Long> totalsByPlayer = stats.stream()
                .collect(Collectors.groupingBy(
                        PlayerMatchStatistic::getPlayerId,
                        Collectors.summingLong(stat -> valueForRanking(stat, type))
                ));

        Comparator<Map.Entry<String, Long>> comparator = type == RankingType.FOULS
                ? Map.Entry.comparingByValue()
                : Map.Entry.<String, Long>comparingByValue().reversed();

        List<RankingEntryResponse> entries = new java.util.ArrayList<>();
        int position = 1;
        for (Map.Entry<String, Long> entry : totalsByPlayer.entrySet().stream()
                .sorted(comparator)
                .limit(Math.max(limit, 1))
                .toList()) {
            entries.add(new RankingEntryResponse(position++, entry.getKey(), entry.getValue()));
        }

        return new RankingResponse(type.name(), tournamentId, entries);
    }

    @Override
    public TournamentStandingsResponse getTournamentStandings(String tournamentId) {
        List<String> teamIds = fetchTournamentStats(tournamentId).stream()
                .map(PlayerMatchStatistic::getTeamId)
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
    public TeamStatisticsResponse getTeamStatisticsInActiveTournament(String teamId) {
        String activeTournamentId = tournamentClient.getActiveTournamentId();
        return buildTeamStatistics(teamId, activeTournamentId);
    }

    @Override
    public TournamentRecognitionResponse generateTournamentRecognitions(String tournamentId) {
        List<PlayerMatchStatistic> tournamentStats = fetchTournamentStats(tournamentId);

        // Maximo goleador: TODOS los jugadores empatados en el primer lugar.
        Map<String, Integer> goalsByPlayer = tournamentStats.stream()
                .collect(Collectors.groupingBy(PlayerMatchStatistic::getPlayerId,
                        Collectors.summingInt(PlayerMatchStatistic::getGoals)));

        long maxGoals = goalsByPlayer.values().stream().mapToLong(Integer::longValue).max().orElse(0);
        List<String> topScorerIds = goalsByPlayer.entrySet().stream()
                .filter(e -> e.getValue() == maxGoals && maxGoals > 0)
                .map(Map.Entry::getKey)
                .toList();

        // Malla menos vencida: TODOS los equipos empatados con menos goles en contra.
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

        Optional<TournamentRecognition> existingDocument = recognitionRepository.findByTournamentId(tournamentId);

        TournamentRecognitionRecord domain = TournamentRecognitionRecord.builder()
                .tournamentId(tournamentId)
                .topScorerPlayerIds(topScorerIds)
                .topScorersGoals(maxGoals)
                .bestDefenseTeamIds(bestDefenseIds)
                .bestDefenseGoalsAgainst(minGoalsAgainst)
                .generatedAt(LocalDateTime.now())
                .build();

        TournamentRecognition documentToSave = recognitionMapper.toDocument(domain);
        // Si ya existia un reconocimiento para este torneo, conservamos su _id
        // de Mongo para que el save() lo REEMPLACE en vez de crear uno nuevo.
        existingDocument.ifPresent(existing -> documentToSave.setId(existing.getId()));

        recognitionRepository.save(documentToSave);

        return toRecognitionResponse(domain);
    }

    @Override
    public TournamentRecognitionResponse getTournamentRecognitions(String tournamentId) {
        TournamentRecognitionRecord domain = recognitionRepository.findByTournamentId(tournamentId)
                .map(recognitionMapper::toDomain)
                .orElseThrow(() -> new RecognitionNotFoundException(tournamentId));
        return toRecognitionResponse(domain);
    }

    @Override
    public GoalkeeperRankingResponse getGoalkeeperRanking(String tournamentId, int limit) {
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

        List<GoalkeeperRankingResponse.Entry> entries = new java.util.ArrayList<>();
        int position = 1;
        for (Map.Entry<String, Long> entry : goalsConcededByGoalkeeper.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(Math.max(limit, 1))
                .toList()) {
            entries.add(new GoalkeeperRankingResponse.Entry(position++, entry.getKey(), entry.getValue()));
        }

        return new GoalkeeperRankingResponse(tournamentId, entries);
    }

    @Override
    public TotalResponse getPlayerTotalAssists(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        long total = stats.stream().mapToInt(PlayerMatchStatistic::getAssists).sum();
        return new TotalResponse(playerId, tournamentId, "totalAssists", total, stats.size());
    }

    // ---------- Jugador: totales y tarjetas ----------

    @Override
    public TotalResponse getPlayerTotalGoals(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        long total = stats.stream().mapToInt(PlayerMatchStatistic::getGoals).sum();
        return new TotalResponse(playerId, tournamentId, "totalGoals", total, stats.size());
    }

    @Override
    public TotalResponse getPlayerTotalFouls(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        long total = stats.stream().mapToInt(PlayerMatchStatistic::getFoulsCommitted).sum();
        return new TotalResponse(playerId, tournamentId, "totalFouls", total, stats.size());
    }

    @Override
    public PlayerCardsResponse getPlayerCards(String playerId, String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchPlayerStats(playerId, tournamentId);
        long yellow = stats.stream().mapToInt(PlayerMatchStatistic::getYellowCards).sum();
        long red = stats.stream().mapToInt(PlayerMatchStatistic::getRedCards).sum();
        return new PlayerCardsResponse(playerId, tournamentId, yellow, red);
    }

    // ---------- Equipo ----------

    @Override
    public TeamMatchRecordResponse getTeamMatchRecord(String teamId, String tournamentId) {
        Map<String, MatchResult> resultByMatch = resultByMatchForTeam(teamId, tournamentId);
        long played = resultByMatch.size();
        long wins = resultByMatch.values().stream().filter(r -> r == MatchResult.WON).count();
        long draws = resultByMatch.values().stream().filter(r -> r == MatchResult.DRAWN).count();
        long losses = resultByMatch.values().stream().filter(r -> r == MatchResult.LOST).count();

        double winRate = played == 0 ? 0.0 : round((wins * 100.0) / played);
        double drawRate = played == 0 ? 0.0 : round((draws * 100.0) / played);
        double lossRate = played == 0 ? 0.0 : round((losses * 100.0) / played);

        return new TeamMatchRecordResponse(teamId, tournamentId, played, wins, draws, losses,
                winRate, drawRate, lossRate);
    }

    @Override
    public TeamAverageResponse getTeamAverageGoals(String teamId, String tournamentId) {
        List<PlayerMatchStatistic> teamStats = fetchTeamStats(teamId, tournamentId);
        long matchesPlayed = teamStats.stream().map(PlayerMatchStatistic::getMatchId).distinct().count();
        long totalGoals = teamStats.stream().mapToInt(PlayerMatchStatistic::getGoals).sum();
        double average = matchesPlayed == 0 ? 0.0 : round((double) totalGoals / matchesPlayed);
        return new TeamAverageResponse(teamId, tournamentId, "averageGoalsPerMatch", average, matchesPlayed);
    }

    @Override
    public TeamAverageResponse getTeamAverageFouls(String teamId, String tournamentId) {
        List<PlayerMatchStatistic> teamStats = fetchTeamStats(teamId, tournamentId);
        long matchesPlayed = teamStats.stream().map(PlayerMatchStatistic::getMatchId).distinct().count();
        long totalFouls = teamStats.stream().mapToInt(PlayerMatchStatistic::getFoulsCommitted).sum();
        double average = matchesPlayed == 0 ? 0.0 : round((double) totalFouls / matchesPlayed);
        return new TeamAverageResponse(teamId, tournamentId, "averageFoulsPerMatch", average, matchesPlayed);
    }

    @Override
    public TotalResponse getTeamTotalFouls(String teamId, String tournamentId) {
        List<PlayerMatchStatistic> teamStats = fetchTeamStats(teamId, tournamentId);
        long matchesPlayed = teamStats.stream().map(PlayerMatchStatistic::getMatchId).distinct().count();
        long totalFouls = teamStats.stream().mapToInt(PlayerMatchStatistic::getFoulsCommitted).sum();
        return new TotalResponse(teamId, tournamentId, "totalFouls", totalFouls, matchesPlayed);
    }

    @Override
    public TeamGoalsResponse getTeamGoals(String teamId, String tournamentId) {
        TeamStatisticsResponse stats = buildTeamStatistics(teamId, tournamentId);
        return new TeamGoalsResponse(teamId, tournamentId, stats.goalsFor(), stats.goalsAgainst(),
                stats.goalDifference());
    }

    // ---------- Torneo (agregados por partido) ----------

    @Override
    public TournamentMatchAveragesResponse getTournamentMatchAverages(String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchTournamentStats(tournamentId);
        long matchesPlayed = stats.stream().map(PlayerMatchStatistic::getMatchId).distinct().count();

        if (matchesPlayed == 0) {
            return new TournamentMatchAveragesResponse(tournamentId, 0, 0.0, 0.0, 0.0);
        }

        long totalGoals = stats.stream().mapToInt(PlayerMatchStatistic::getGoals).sum();
        long totalFouls = stats.stream().mapToInt(PlayerMatchStatistic::getFoulsCommitted).sum();
        long totalCards = stats.stream()
                .mapToInt(s -> s.getYellowCards() + s.getRedCards())
                .sum();

        return new TournamentMatchAveragesResponse(
                tournamentId,
                matchesPlayed,
                round((double) totalGoals / matchesPlayed),
                round((double) totalFouls / matchesPlayed),
                round((double) totalCards / matchesPlayed));
    }

    @Override
    public CardsTotalResponse getTournamentCardsTotal(String tournamentId) {
        List<PlayerMatchStatistic> stats = fetchTournamentStats(tournamentId);
        long yellow = stats.stream().mapToInt(PlayerMatchStatistic::getYellowCards).sum();
        long red = stats.stream().mapToInt(PlayerMatchStatistic::getRedCards).sum();
        return new CardsTotalResponse("tournament", tournamentId, yellow, red);
    }

    // ---------- Partido ----------

    @Override
    public CardsTotalResponse getMatchCardsTotal(String matchId) {
        List<PlayerMatchStatistic> stats = fetchMatchStats(matchId);
        long yellow = stats.stream().mapToInt(PlayerMatchStatistic::getYellowCards).sum();
        long red = stats.stream().mapToInt(PlayerMatchStatistic::getRedCards).sum();
        return new CardsTotalResponse("match", matchId, yellow, red);
    }

    @Override
    public MatchResultResponse getMatchResult(String matchId) {
        List<PlayerMatchStatistic> stats = fetchMatchStats(matchId);

        String tournamentId = stats.stream()
                .map(PlayerMatchStatistic::getTournamentId)
                .findFirst()
                .orElse(null);

        List<MatchResultResponse.TeamResult> teamResults = stats.stream()
                .collect(Collectors.toMap(
                        PlayerMatchStatistic::getTeamId,
                        PlayerMatchStatistic::getResult,
                        (a, b) -> a))
                .entrySet().stream()
                .map(e -> new MatchResultResponse.TeamResult(e.getKey(), e.getValue()))
                .toList();

        return new MatchResultResponse(matchId, tournamentId, teamResults);
    }

    // ---------- Helpers privados: acceso a datos (mapean Documento -> Dominio) ----------

    private List<PlayerMatchStatistic> fetchPlayerStats(String playerId, String tournamentId) {
        List<PlayerMatchStat> documents = tournamentId == null
                ? repository.findByPlayerId(playerId)
                : repository.findByPlayerIdAndTournamentId(playerId, tournamentId);
        return playerMatchStatMapper.toDomainList(documents);
    }

    private List<PlayerMatchStatistic> fetchTeamStats(String teamId, String tournamentId) {
        List<PlayerMatchStat> documents = tournamentId == null
                ? repository.findByTeamId(teamId)
                : repository.findByTeamIdAndTournamentId(teamId, tournamentId);
        return playerMatchStatMapper.toDomainList(documents);
    }

    private List<PlayerMatchStatistic> fetchTournamentStats(String tournamentId) {
        List<PlayerMatchStat> documents = tournamentId == null
                ? repository.findAll()
                : repository.findByTournamentId(tournamentId);
        return playerMatchStatMapper.toDomainList(documents);
    }

    private List<PlayerMatchStatistic> fetchMatchStats(String matchId) {
        return playerMatchStatMapper.toDomainList(repository.findByMatchId(matchId));
    }

    // ---------- Helpers privados: logica de negocio (solo dominio) ----------

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

    /**
     * Calcula todas las estadisticas de un equipo (partidos, puntos, goles)
     * para un torneo puntual.
     */
    private TeamStatisticsResponse buildTeamStatistics(String teamId, String tournamentId) {
        List<PlayerMatchStatistic> teamStats = fetchTeamStats(teamId, tournamentId);

        // Cada partido que el equipo jugo aparece repetido (una fila por
        // jugador), por eso agrupamos por matchId para no contar de mas.
        Map<String, MatchResult> resultByMatch = resultByMatchForTeam(teamId, tournamentId);

        long played = resultByMatch.size();
        long wins = resultByMatch.values().stream().filter(r -> r == MatchResult.WON).count();
        long draws = resultByMatch.values().stream().filter(r -> r == MatchResult.DRAWN).count();
        long losses = resultByMatch.values().stream().filter(r -> r == MatchResult.LOST).count();

        long goalsFor = teamStats.stream().mapToInt(PlayerMatchStatistic::getGoals).sum();

        // Goles en contra: para cada partido de este equipo, se suman los
        // goles de los jugadores del equipo RIVAL en ese mismo partido.
        long goalsAgainst = resultByMatch.keySet().stream()
                .mapToLong(matchId -> fetchMatchStats(matchId).stream()
                        .filter(s -> !s.getTeamId().equals(teamId))
                        .mapToInt(PlayerMatchStatistic::getGoals)
                        .sum())
                .sum();

        long points = (wins * 3) + draws;

        return new TeamStatisticsResponse(
                teamId, tournamentId, played, wins, draws, losses,
                goalsFor, goalsAgainst, goalsFor - goalsAgainst, points);
    }

    private TournamentRecognitionResponse toRecognitionResponse(TournamentRecognitionRecord recognition) {
        List<TournamentRecognitionResponse.PlayerGoals> topScorers = recognition.getTopScorerPlayerIds().stream()
                .map(id -> new TournamentRecognitionResponse.PlayerGoals(id, recognition.getTopScorersGoals()))
                .toList();

        List<TournamentRecognitionResponse.TeamGoalsAgainst> bestDefenses = recognition.getBestDefenseTeamIds()
                .stream()
                .map(id -> new TournamentRecognitionResponse.TeamGoalsAgainst(
                        id, recognition.getBestDefenseGoalsAgainst()))
                .toList();

        return new TournamentRecognitionResponse(
                recognition.getTournamentId(),
                topScorers,
                recognition.getTopScorersGoals(),
                bestDefenses,
                recognition.getBestDefenseGoalsAgainst(),
                recognition.getGeneratedAt());
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}

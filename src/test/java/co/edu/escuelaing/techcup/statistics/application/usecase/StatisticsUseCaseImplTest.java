package co.edu.escuelaing.techcup.statistics.application.usecase;

import co.edu.escuelaing.techcup.statistics.domain.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.domain.exception.RecognitionNotFoundException;
import co.edu.escuelaing.techcup.statistics.domain.model.*;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.feign.TournamentClient;
import co.edu.escuelaing.techcup.statistics.application.mapper.PlayerMatchStatMapper;
import co.edu.escuelaing.techcup.statistics.application.mapper.PlayerMatchStatMapperImpl;
import co.edu.escuelaing.techcup.statistics.application.mapper.TournamentRecognitionMapper;
import co.edu.escuelaing.techcup.statistics.application.mapper.TournamentRecognitionMapperImpl;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.PlayerMatchStatDocument;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.TournamentRecognitionDocument;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.PlayerMatchStatRepository;
import co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo.TournamentRecognitionRepository;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.request.MatchStatEventRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsUseCaseImplTest {

    @Mock private PlayerMatchStatRepository repository;
    @Mock private TournamentClient tournamentClient;
    @Mock private TournamentRecognitionRepository recognitionRepository;

    private StatisticsUseCaseImpl statisticsService;
    private final PlayerMatchStatMapper playerMatchStatMapper = new PlayerMatchStatMapperImpl();
    private final TournamentRecognitionMapper recognitionMapper = new TournamentRecognitionMapperImpl();

    private static final String PLAYER_ID = "player-1";
    private static final String TEAM_ID = "team-10";
    private static final String TOURNAMENT_ID = "tournament-100";

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsUseCaseImpl(
                repository, recognitionRepository, tournamentClient, playerMatchStatMapper, recognitionMapper);
    }

    private PlayerMatchStatDocument stat(String playerId, String teamId, String matchId, MatchResult result,
                                          int goals, int fouls, int minutes) {
        return PlayerMatchStatDocument.builder()
                .playerId(playerId).teamId(teamId).matchId(matchId).tournamentId(TOURNAMENT_ID)
                .result(result).goals(goals).foulsCommitted(fouls).minutesPlayed(minutes).build();
    }

    private PlayerMatchStatDocument statFull(String playerId, String teamId, String matchId, MatchResult result,
                                              int goals, int fouls, int minutes, int yellow, int red, int assists) {
        return PlayerMatchStatDocument.builder()
                .playerId(playerId).teamId(teamId).matchId(matchId).tournamentId(TOURNAMENT_ID)
                .result(result).goals(goals).foulsCommitted(fouls).minutesPlayed(minutes)
                .yellowCards(yellow).redCards(red).assists(assists).build();
    }

    private PlayerMatchStatDocument goalkeeperStat(String playerId, String teamId, String matchId, MatchResult result) {
        return PlayerMatchStatDocument.builder()
                .playerId(playerId).teamId(teamId).matchId(matchId).tournamentId(TOURNAMENT_ID)
                .result(result).goalkeeper(true).minutesPlayed(90).goals(0).foulsCommitted(0).build();
    }

    // ======================== registerMatchStat ========================

    @Test
    void registerMatchStat_deberiaGuardarCuandoNoEsDuplicado() {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, TEAM_ID, "match-500", TOURNAMENT_ID, MatchResult.WON,
                2, 1, 0, 3, 90, 1, false);
        when(repository.existsByPlayerIdAndMatchId(PLAYER_ID, "match-500")).thenReturn(false);
        statisticsService.registerMatchStat(playerMatchStatMapper.toDomain(request));
        ArgumentCaptor<PlayerMatchStatDocument> captor = ArgumentCaptor.forClass(PlayerMatchStatDocument.class);
        verify(repository).save(captor.capture());
        PlayerMatchStatDocument saved = captor.getValue();
        assertThat(saved.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(saved.getGoals()).isEqualTo(2);
    }

    @Test
    void registerMatchStat_deberiaLanzarExcepcionSiYaExiste() {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, TEAM_ID, "match-500", TOURNAMENT_ID, MatchResult.WON,
                2, 0, 0, 1, 90, 0, false);
        when(repository.existsByPlayerIdAndMatchId(PLAYER_ID, "match-500")).thenReturn(true);
        assertThrows(DuplicateMatchStatException.class,
                () -> statisticsService.registerMatchStat(playerMatchStatMapper.toDomain(request)));
        verify(repository, never()).save(any());
    }

    // ======================== getAverageWinRate ========================

    @Test
    void getAverageWinRate_deberiaCalcularPorcentaje() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 0, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m2", MatchResult.WON, 0, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m3", MatchResult.LOST, 0, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m4", MatchResult.DRAWN, 0, 0, 90)));
        PlayerAverageResult result = statisticsService.getAverageWinRate(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.value()).isEqualTo(50.0);
        assertThat(result.matchesConsidered()).isEqualTo(4);
    }

    @Test
    void getAverageWinRate_deberiaSerCeroCuandoNoHayPartidos() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of());
        PlayerAverageResult result = statisticsService.getAverageWinRate(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.value()).isEqualTo(0.0);
        assertThat(result.matchesConsidered()).isEqualTo(0);
    }

    @Test
    void getAverageWinRate_conNullTournamentId_deberiaBuscarTodos() {
        when(repository.findByPlayerId(PLAYER_ID)).thenReturn(List.of(
                stat(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 0, 0, 90)));
        PlayerAverageResult result = statisticsService.getAverageWinRate(PLAYER_ID, null);
        assertThat(result.value()).isEqualTo(100.0);
        verify(repository).findByPlayerId(PLAYER_ID);
        verify(repository, never()).findByPlayerIdAndTournamentId(any(), any());
    }

    // ======================== getAverageGoals ========================

    @Test
    void getAverageGoals_deberiaCalcularElPromedioYRedondear() {
        List<PlayerMatchStatDocument> stats = List.of(
                stat(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m2", MatchResult.LOST, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m3", MatchResult.WON, 2, 0, 90));
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(stats);
        PlayerAverageResult response = statisticsService.getAverageGoals(PLAYER_ID, TOURNAMENT_ID);
        assertThat(response.value()).isEqualTo(1.67);
    }

    @Test
    void getAverageGoals_deberiaSerCeroCuandoNoHayStats() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of());
        PlayerAverageResult result = statisticsService.getAverageGoals(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.value()).isEqualTo(0.0);
        assertThat(result.matchesConsidered()).isEqualTo(0);
    }

    // ======================== getAverageFouls ========================

    @Test
    void getAverageFouls_deberiaCalcularPromedio() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 0, 2, 90),
                stat(PLAYER_ID, TEAM_ID, "m2", MatchResult.WON, 0, 4, 90)));
        PlayerAverageResult result = statisticsService.getAverageFouls(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.value()).isEqualTo(3.0);
    }

    @Test
    void getAverageFouls_conListaVacia_deberiaSerCero() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of());
        PlayerAverageResult result = statisticsService.getAverageFouls(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.value()).isEqualTo(0.0);
    }

    // ======================== getAverageMinutesPlayed ========================

    @Test
    void getAverageMinutesPlayed_deberiaCalcularPromedio() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 0, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m2", MatchResult.WON, 0, 0, 45)));
        PlayerAverageResult result = statisticsService.getAverageMinutesPlayed(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.value()).isEqualTo(67.5);
    }

    @Test
    void getAverageMinutesPlayed_conListaVacia_deberiaSerCero() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of());
        PlayerAverageResult result = statisticsService.getAverageMinutesPlayed(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.value()).isEqualTo(0.0);
    }

    // ======================== getMatchesPlayed ========================

    @Test
    void getMatchesPlayed_deberiaContarPartidos() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 0, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m2", MatchResult.LOST, 0, 0, 90)));
        MatchesPlayedResult result = statisticsService.getMatchesPlayed(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.matchesPlayed()).isEqualTo(2);
    }

    @Test
    void getMatchesPlayed_conListaVacia_deberiaSerCero() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of());
        MatchesPlayedResult result = statisticsService.getMatchesPlayed(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.matchesPlayed()).isEqualTo(0);
    }

    // ======================== getRanking ========================

    @Test
    void getRanking_goals_deberiaOrdenarDescendente() {
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", "t1", "m1", MatchResult.WON, 5, 0, 90, 0, 0, 0),
                statFull("p2", "t1", "m1", MatchResult.WON, 3, 0, 90, 0, 0, 0),
                statFull("p3", "t2", "m1", MatchResult.LOST, 1, 0, 90, 0, 0, 0)));
        RankingResult result = statisticsService.getRanking(RankingType.GOALS, TOURNAMENT_ID, 10);
        assertThat(result.entries()).hasSize(3);
        assertThat(result.entries().get(0).playerId()).isEqualTo("p1");
        assertThat(result.entries().get(0).value()).isEqualTo(5);
    }

    @Test
    void getRanking_fouls_deberiaOrdenarAscendente() {
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", "t1", "m1", MatchResult.WON, 0, 10, 90, 0, 0, 0),
                statFull("p2", "t1", "m1", MatchResult.WON, 0, 3, 90, 0, 0, 0),
                statFull("p3", "t2", "m1", MatchResult.LOST, 0, 7, 90, 0, 0, 0)));
        RankingResult result = statisticsService.getRanking(RankingType.FOULS, TOURNAMENT_ID, 10);
        assertThat(result.entries().get(0).playerId()).isEqualTo("p2");
        assertThat(result.entries().get(0).value()).isEqualTo(3);
    }

    @Test
    void getRanking_minutes_deberiaSumarMinutos() {
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", "t1", "m1", MatchResult.WON, 0, 0, 90, 0, 0, 0),
                statFull("p1", "t1", "m2", MatchResult.WON, 0, 0, 45, 0, 0, 0),
                statFull("p2", "t2", "m1", MatchResult.LOST, 0, 0, 90, 0, 0, 0)));
        RankingResult result = statisticsService.getRanking(RankingType.MINUTES, TOURNAMENT_ID, 10);
        assertThat(result.entries().get(0).playerId()).isEqualTo("p1");
        assertThat(result.entries().get(0).value()).isEqualTo(135);
    }

    @Test
    void getRanking_wins_deberiaContarVictorias() {
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", "t1", "m1", MatchResult.WON, 0, 0, 90, 0, 0, 0),
                statFull("p1", "t1", "m2", MatchResult.WON, 0, 0, 90, 0, 0, 0),
                statFull("p1", "t1", "m3", MatchResult.LOST, 0, 0, 90, 0, 0, 0),
                statFull("p2", "t2", "m1", MatchResult.WON, 0, 0, 90, 0, 0, 0)));
        RankingResult result = statisticsService.getRanking(RankingType.WINS, TOURNAMENT_ID, 10);
        assertThat(result.entries().get(0).playerId()).isEqualTo("p1");
        assertThat(result.entries().get(0).value()).isEqualTo(2);
    }

    @Test
    void getRanking_conTorneoNull_deberiaBuscarTodos() {
        when(repository.findAll()).thenReturn(List.of(
                statFull("p1", "t1", "m1", MatchResult.WON, 5, 0, 90, 0, 0, 0)));
        RankingResult result = statisticsService.getRanking(RankingType.GOALS, null, 10);
        assertThat(result.entries()).hasSize(1);
        verify(repository).findAll();
        verify(repository, never()).findByTournamentId(any());
    }

    @Test
    void getRanking_conLimitMenorA2_deberiaUsarAlMenos1() {
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", "t1", "m1", MatchResult.WON, 5, 0, 90, 0, 0, 0),
                statFull("p2", "t1", "m1", MatchResult.WON, 3, 0, 90, 0, 0, 0)));
        RankingResult result = statisticsService.getRanking(RankingType.GOALS, TOURNAMENT_ID, 0);
        assertThat(result.entries()).hasSize(1);
    }

    // ======================== getTournamentStandings ========================

    @Test
    void getTournamentStandings_deberiaOrdenarPorPuntosYGoalDifference() {
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", "teamA", "m1", MatchResult.WON, 3, 0, 90, 0, 0, 0),
                statFull("p2", "teamA", "m1", MatchResult.WON, 0, 0, 90, 0, 0, 0),
                statFull("p3", "teamB", "m1", MatchResult.LOST, 0, 0, 90, 0, 0, 0)));
        when(repository.findByTeamIdAndTournamentId("teamA", TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", "teamA", "m1", MatchResult.WON, 3, 0, 90, 0, 0, 0),
                statFull("p2", "teamA", "m1", MatchResult.WON, 0, 0, 90, 0, 0, 0)));
        when(repository.findByTeamIdAndTournamentId("teamB", TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p3", "teamB", "m1", MatchResult.LOST, 0, 0, 90, 0, 0, 0)));
        when(repository.findByMatchId("m1")).thenReturn(List.of(
                statFull("p1", "teamA", "m1", MatchResult.WON, 3, 0, 90, 0, 0, 0),
                statFull("p2", "teamA", "m1", MatchResult.WON, 0, 0, 90, 0, 0, 0),
                statFull("p3", "teamB", "m1", MatchResult.LOST, 0, 0, 90, 0, 0, 0)));
        TournamentStandingsResult result = statisticsService.getTournamentStandings(TOURNAMENT_ID);
        assertThat(result.standings()).hasSize(2);
        assertThat(result.standings().get(0).teamId()).isEqualTo("teamA");
        assertThat(result.standings().get(0).points()).isEqualTo(3);
    }

    // ======================== getTeamStatistics ========================

    @Test
    void getTeamStatistics_deberiaResolverElTorneoActivo() {
        when(tournamentClient.getActiveTournamentId()).thenReturn(TOURNAMENT_ID);
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat("p2", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90)));
        when(repository.findByMatchId("m1")).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat("p2", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat("p9", "team99", "m1", MatchResult.LOST, 1, 0, 90)));
        var response = statisticsService.getTeamStatistics(TEAM_ID, null);
        assertThat(response.points()).isEqualTo(3L);
    }

    @Test
    void getTeamStatistics_conTournamentIdDirecto_noDebeLlamarTorneos() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90)));
        when(repository.findByMatchId("m1")).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat("p9", "team99", "m1", MatchResult.LOST, 0, 0, 90)));
        var response = statisticsService.getTeamStatistics(TEAM_ID, TOURNAMENT_ID);
        assertThat(response.points()).isEqualTo(3L);
        verify(tournamentClient, never()).getActiveTournamentId();
    }

    // ======================== generateTournamentRecognitions ========================

    @Test
    void generateTournamentRecognitions_deberiaPublicarTodosLosGoleadoresEmpatados() {
        PlayerMatchStatDocument s1 = stat("p20", "teamA", "m300", MatchResult.WON, 3, 0, 90);
        PlayerMatchStatDocument s2 = stat("p22", "teamC", "m301", MatchResult.WON, 3, 0, 90);
        PlayerMatchStatDocument o1 = stat("p21", "teamB", "m300", MatchResult.LOST, 0, 0, 90);
        PlayerMatchStatDocument o2 = stat("p23", "teamD", "m301", MatchResult.LOST, 0, 0, 90);
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(s1, o1, s2, o2));
        when(repository.findByTeamIdAndTournamentId("teamA", TOURNAMENT_ID)).thenReturn(List.of(s1));
        when(repository.findByTeamIdAndTournamentId("teamB", TOURNAMENT_ID)).thenReturn(List.of(o1));
        when(repository.findByTeamIdAndTournamentId("teamC", TOURNAMENT_ID)).thenReturn(List.of(s2));
        when(repository.findByTeamIdAndTournamentId("teamD", TOURNAMENT_ID)).thenReturn(List.of(o2));
        when(repository.findByMatchId("m300")).thenReturn(List.of(s1, o1));
        when(repository.findByMatchId("m301")).thenReturn(List.of(s2, o2));
        when(recognitionRepository.findByTournamentId(TOURNAMENT_ID)).thenReturn(Optional.empty());
        when(recognitionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        TournamentRecognitionRecord response = statisticsService.generateTournamentRecognitions(TOURNAMENT_ID);
        assertThat(response.getTopScorersGoals()).isEqualTo(3L);
    }

    @Test
    void generateTournamentRecognitions_sinGoles_noDebeTenerGoleadores() {
        PlayerMatchStatDocument s1 = stat("p20", "teamA", "m300", MatchResult.DRAWN, 0, 0, 90);
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(s1));
        when(repository.findByTeamIdAndTournamentId("teamA", TOURNAMENT_ID)).thenReturn(List.of(s1));
        when(repository.findByMatchId("m300")).thenReturn(List.of(s1));
        when(recognitionRepository.findByTournamentId(TOURNAMENT_ID)).thenReturn(Optional.empty());
        when(recognitionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        TournamentRecognitionRecord response = statisticsService.generateTournamentRecognitions(TOURNAMENT_ID);
        assertThat(response.getTopScorersGoals()).isEqualTo(0L);
        assertThat(response.getTopScorerPlayerIds()).isEmpty();
    }

    @Test
    void generateTournamentRecognitions_conRegistroExistente_deberiaActualizar() {
        PlayerMatchStatDocument s1 = stat("p20", "teamA", "m300", MatchResult.WON, 2, 0, 90);
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(s1));
        when(repository.findByTeamIdAndTournamentId("teamA", TOURNAMENT_ID)).thenReturn(List.of(s1));
        when(repository.findByMatchId("m300")).thenReturn(List.of(s1));
        TournamentRecognitionDocument existing = new TournamentRecognitionDocument();
        existing.setId("existing-id");
        when(recognitionRepository.findByTournamentId(TOURNAMENT_ID)).thenReturn(Optional.of(existing));
        when(recognitionRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        statisticsService.generateTournamentRecognitions(TOURNAMENT_ID);
        ArgumentCaptor<TournamentRecognitionDocument> captor = ArgumentCaptor.forClass(TournamentRecognitionDocument.class);
        verify(recognitionRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isEqualTo("existing-id");
    }

    // ======================== getTournamentRecognitions ========================

    @Test
    void getTournamentRecognitions_deberiaLanzarExcepcionSiNoSeHaGenerado() {
        when(recognitionRepository.findByTournamentId(TOURNAMENT_ID)).thenReturn(Optional.empty());
        assertThrows(RecognitionNotFoundException.class,
                () -> statisticsService.getTournamentRecognitions(TOURNAMENT_ID));
    }

    @Test
    void getTournamentRecognitions_deberiaRetornarReconocimientos() {
        TournamentRecognitionDocument doc = new TournamentRecognitionDocument();
        doc.setTournamentId(TOURNAMENT_ID);
        doc.setTopScorersGoals(5L);
        when(recognitionRepository.findByTournamentId(TOURNAMENT_ID)).thenReturn(Optional.of(doc));
        TournamentRecognitionRecord result = statisticsService.getTournamentRecognitions(TOURNAMENT_ID);
        assertThat(result.getTopScorersGoals()).isEqualTo(5L);
    }

    // ======================== getGoalkeeperRanking ========================

    @Test
    void getGoalkeeperRanking_deberiaCalcularGolesRecibidos() {
        PlayerMatchStatDocument gk = goalkeeperStat("gk1", "teamA", "m1", MatchResult.WON);
        PlayerMatchStatDocument shooter1 = statFull("p1", "teamB", "m1", MatchResult.LOST, 2, 0, 90, 0, 0, 0);
        PlayerMatchStatDocument shooter2 = statFull("p2", "teamB", "m1", MatchResult.LOST, 1, 0, 90, 0, 0, 0);
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(gk, shooter1, shooter2));
        GoalkeeperRankingResult result = statisticsService.getGoalkeeperRanking(TOURNAMENT_ID, 10);
        assertThat(result.entries()).hasSize(1);
        assertThat(result.entries().get(0).playerId()).isEqualTo("gk1");
        assertThat(result.entries().get(0).goalsConceded()).isEqualTo(3);
    }

    @Test
    void getGoalkeeperRanking_sinArqueros_deberiaRetornarVacio() {
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", "teamA", "m1", MatchResult.WON, 2, 0, 90, 0, 0, 0)));
        GoalkeeperRankingResult result = statisticsService.getGoalkeeperRanking(TOURNAMENT_ID, 10);
        assertThat(result.entries()).isEmpty();
    }

    @Test
    void getGoalkeeperRanking_conTorneoNull_deberiaBuscarTodos() {
        when(repository.findAll()).thenReturn(List.of());
        GoalkeeperRankingResult result = statisticsService.getGoalkeeperRanking(null, 10);
        assertThat(result.entries()).isEmpty();
        verify(repository).findAll();
    }

    // ======================== getPlayerTotal* ========================

    @Test
    void getPlayerTotalAssists_deberiaSumarAsistencias() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of(
                statFull(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 0, 0, 90, 0, 0, 3),
                statFull(PLAYER_ID, TEAM_ID, "m2", MatchResult.WON, 0, 0, 90, 0, 0, 2)));
        TotalResult result = statisticsService.getPlayerTotalAssists(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.total()).isEqualTo(5);
    }

    @Test
    void getPlayerTotalGoals_deberiaSumarGoles() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of(
                statFull(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 2, 0, 90, 0, 0, 0),
                statFull(PLAYER_ID, TEAM_ID, "m2", MatchResult.WON, 3, 0, 90, 0, 0, 0)));
        TotalResult result = statisticsService.getPlayerTotalGoals(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.total()).isEqualTo(5);
    }

    @Test
    void getPlayerTotalFouls_deberiaSumarFaltas() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of(
                statFull(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 0, 4, 90, 0, 0, 0),
                statFull(PLAYER_ID, TEAM_ID, "m2", MatchResult.WON, 0, 2, 90, 0, 0, 0)));
        TotalResult result = statisticsService.getPlayerTotalFouls(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.total()).isEqualTo(6);
    }

    // ======================== getPlayerCards ========================

    @Test
    void getPlayerCards_deberiaSumarTarjetas() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of(
                statFull(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 0, 0, 90, 1, 0, 0),
                statFull(PLAYER_ID, TEAM_ID, "m2", MatchResult.WON, 0, 0, 90, 0, 1, 0)));
        PlayerCardsResult result = statisticsService.getPlayerCards(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.yellowCards()).isEqualTo(1);
        assertThat(result.redCards()).isEqualTo(1);
    }

    @Test
    void getPlayerCards_conListaVacia_deberiaSerCero() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of());
        PlayerCardsResult result = statisticsService.getPlayerCards(PLAYER_ID, TOURNAMENT_ID);
        assertThat(result.yellowCards()).isEqualTo(0);
        assertThat(result.redCards()).isEqualTo(0);
    }

    // ======================== getTeamMatchRecord ========================

    @Test
    void getTeamMatchRecord_deberiaCalcularRacha() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", TEAM_ID, "m1", MatchResult.WON, 0, 0, 90, 0, 0, 0),
                statFull("p2", TEAM_ID, "m1", MatchResult.WON, 0, 0, 90, 0, 0, 0),
                statFull("p3", TEAM_ID, "m2", MatchResult.LOST, 0, 0, 90, 0, 0, 0),
                statFull("p4", TEAM_ID, "m2", MatchResult.LOST, 0, 0, 90, 0, 0, 0),
                statFull("p5", TEAM_ID, "m3", MatchResult.DRAWN, 0, 0, 90, 0, 0, 0)));
        TeamMatchRecordResult result = statisticsService.getTeamMatchRecord(TEAM_ID, TOURNAMENT_ID);
        assertThat(result.matchesPlayed()).isEqualTo(3);
        assertThat(result.wins()).isEqualTo(1);
        assertThat(result.draws()).isEqualTo(1);
        assertThat(result.losses()).isEqualTo(1);
        assertThat(result.winRatePercentage()).isEqualTo(33.33);
        assertThat(result.drawRatePercentage()).isEqualTo(33.33);
        assertThat(result.lossRatePercentage()).isEqualTo(33.33);
    }

    @Test
    void getTeamMatchRecord_sinPartidos_deberiaSerCero() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of());
        TeamMatchRecordResult result = statisticsService.getTeamMatchRecord(TEAM_ID, TOURNAMENT_ID);
        assertThat(result.matchesPlayed()).isEqualTo(0);
        assertThat(result.winRatePercentage()).isEqualTo(0.0);
        assertThat(result.drawRatePercentage()).isEqualTo(0.0);
        assertThat(result.lossRatePercentage()).isEqualTo(0.0);
    }

    // ======================== getTeamAverage* ========================

    @Test
    void getTeamAverageGoals_deberiaCalcularPromedio() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", TEAM_ID, "m1", MatchResult.WON, 3, 0, 90, 0, 0, 0),
                statFull("p2", TEAM_ID, "m1", MatchResult.WON, 1, 0, 90, 0, 0, 0),
                statFull("p3", TEAM_ID, "m2", MatchResult.LOST, 0, 0, 90, 0, 0, 0)));
        TeamAverageResult result = statisticsService.getTeamAverageGoals(TEAM_ID, TOURNAMENT_ID);
        assertThat(result.value()).isEqualTo(2.0);
    }

    @Test
    void getTeamAverageGoals_sinPartidos_deberiaSerCero() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of());
        TeamAverageResult result = statisticsService.getTeamAverageGoals(TEAM_ID, TOURNAMENT_ID);
        assertThat(result.value()).isEqualTo(0.0);
    }

    @Test
    void getTeamAverageFouls_deberiaCalcularPromedio() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", TEAM_ID, "m1", MatchResult.WON, 0, 5, 90, 0, 0, 0),
                statFull("p2", TEAM_ID, "m1", MatchResult.WON, 0, 3, 90, 0, 0, 0)));
        TeamAverageResult result = statisticsService.getTeamAverageFouls(TEAM_ID, TOURNAMENT_ID);
        assertThat(result.value()).isEqualTo(8.0);
    }

    @Test
    void getTeamAverageFouls_sinPartidos_deberiaSerCero() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of());
        TeamAverageResult result = statisticsService.getTeamAverageFouls(TEAM_ID, TOURNAMENT_ID);
        assertThat(result.value()).isEqualTo(0.0);
    }

    // ======================== getTeamTotalFouls ========================

    @Test
    void getTeamTotalFouls_deberiaSumarFaltas() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", TEAM_ID, "m1", MatchResult.WON, 0, 5, 90, 0, 0, 0),
                statFull("p2", TEAM_ID, "m1", MatchResult.WON, 0, 3, 90, 0, 0, 0)));
        TotalResult result = statisticsService.getTeamTotalFouls(TEAM_ID, TOURNAMENT_ID);
        assertThat(result.total()).isEqualTo(8);
    }

    // ======================== getTeamGoals ========================

    @Test
    void getTeamGoals_deberiaCalcularGolesAFavorEnContraYDiferencia() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", TEAM_ID, "m1", MatchResult.WON, 3, 0, 90, 0, 0, 0)));
        when(repository.findByMatchId("m1")).thenReturn(List.of(
                statFull("p1", TEAM_ID, "m1", MatchResult.WON, 3, 0, 90, 0, 0, 0),
                statFull("p9", "team99", "m1", MatchResult.LOST, 1, 0, 90, 0, 0, 0)));
        TeamGoalsResult result = statisticsService.getTeamGoals(TEAM_ID, TOURNAMENT_ID);
        assertThat(result.goalsFor()).isEqualTo(3);
        assertThat(result.goalsAgainst()).isEqualTo(1);
        assertThat(result.goalDifference()).isEqualTo(2);
    }

    // ======================== getTournamentMatchAverages ========================

    @Test
    void getTournamentMatchAverages_deberiaCalcularPromedios() {
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", "t1", "m1", MatchResult.WON, 3, 5, 90, 1, 0, 0),
                statFull("p2", "t2", "m1", MatchResult.LOST, 1, 8, 90, 0, 1, 0)));
        TournamentMatchAveragesResult result = statisticsService.getTournamentMatchAverages(TOURNAMENT_ID);
        assertThat(result.matchesConsidered()).isEqualTo(1);
        assertThat(result.averageGoalsPerMatch()).isEqualTo(4.0);
        assertThat(result.averageFoulsPerMatch()).isEqualTo(13.0);
        assertThat(result.averageCardsPerMatch()).isEqualTo(2.0);
    }

    @Test
    void getTournamentMatchAverages_sinPartidos_deberiaSerCero() {
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of());
        TournamentMatchAveragesResult result = statisticsService.getTournamentMatchAverages(TOURNAMENT_ID);
        assertThat(result.matchesConsidered()).isEqualTo(0);
        assertThat(result.averageGoalsPerMatch()).isEqualTo(0);
    }

    // ======================== getTournamentCardsTotal ========================

    @Test
    void getTournamentCardsTotal_deberiaSumarTarjetas() {
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(
                statFull("p1", "t1", "m1", MatchResult.WON, 0, 0, 90, 2, 1, 0),
                statFull("p2", "t1", "m1", MatchResult.WON, 0, 0, 90, 1, 0, 0)));
        CardsTotalResult result = statisticsService.getTournamentCardsTotal(TOURNAMENT_ID);
        assertThat(result.yellowCards()).isEqualTo(3);
        assertThat(result.redCards()).isEqualTo(1);
    }

    // ======================== getMatchCardsTotal ========================

    @Test
    void getMatchCardsTotal_deberiaSumarTarjetasDelPartido() {
        when(repository.findByMatchId("m1")).thenReturn(List.of(
                statFull("p1", "t1", "m1", MatchResult.WON, 0, 0, 90, 1, 0, 0),
                statFull("p2", "t2", "m1", MatchResult.LOST, 0, 0, 90, 2, 1, 0)));
        CardsTotalResult result = statisticsService.getMatchCardsTotal("m1");
        assertThat(result.yellowCards()).isEqualTo(3);
        assertThat(result.redCards()).isEqualTo(1);
    }

    // ======================== getMatchResult ========================

    @Test
    void getMatchResult_deberiaExtraerResultadosPorEquipo() {
        when(repository.findByMatchId("m1")).thenReturn(List.of(
                statFull("p1", "teamA", "m1", MatchResult.WON, 2, 0, 90, 0, 0, 0),
                statFull("p2", "teamA", "m1", MatchResult.WON, 0, 0, 90, 0, 0, 0),
                statFull("p3", "teamB", "m1", MatchResult.LOST, 0, 0, 90, 0, 0, 0)));
        MatchResultResult result = statisticsService.getMatchResult("m1");
        assertThat(result.teamResults()).hasSize(2);
        assertThat(result.matchId()).isEqualTo("m1");
    }

    @Test
    void getMatchResult_sinEstadisticas_deberiaTenerTournamentIdNulo() {
        when(repository.findByMatchId("m1")).thenReturn(List.of());
        MatchResultResult result = statisticsService.getMatchResult("m1");
        assertThat(result.teamResults()).isEmpty();
    }

    // ======================== Edge: tournamentId null for fetch methods ========================

    @Test
    void getAverageWinRate_conNullTournamentId_usaFindByPlayerId() {
        when(repository.findByPlayerId(PLAYER_ID)).thenReturn(List.of(
                stat(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 0, 0, 90)));
        statisticsService.getAverageWinRate(PLAYER_ID, null);
        verify(repository).findByPlayerId(PLAYER_ID);
    }

    @Test
    void getAverageGoals_conNullTournamentId_usaFindByPlayerId() {
        when(repository.findByPlayerId(PLAYER_ID)).thenReturn(List.of());
        statisticsService.getAverageGoals(PLAYER_ID, null);
        verify(repository).findByPlayerId(PLAYER_ID);
    }

    @Test
    void getTeamAverageFouls_conNullTournamentId_usaFindByTeamId() {
        when(repository.findByTeamId(TEAM_ID)).thenReturn(List.of());
        statisticsService.getTeamAverageFouls(TEAM_ID, null);
        verify(repository).findByTeamId(TEAM_ID);
    }

    @Test
    void getTournamentCardsTotal_conNullTournamentId_usaFindAll() {
        when(repository.findAll()).thenReturn(List.of());
        statisticsService.getTournamentCardsTotal(null);
        verify(repository).findAll();
    }

    @Test
    void getMatchCardsTotal_conPartidoSinDatos_deberiaRetornarCero() {
        when(repository.findByMatchId("m1")).thenReturn(List.of());
        CardsTotalResult result = statisticsService.getMatchCardsTotal("m1");
        assertThat(result.yellowCards()).isEqualTo(0);
        assertThat(result.redCards()).isEqualTo(0);
    }
}

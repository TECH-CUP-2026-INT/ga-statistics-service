package co.edu.escuelaing.techcup.statistics.service;

import co.edu.escuelaing.techcup.statistics.client.TournamentClient;
import co.edu.escuelaing.techcup.statistics.dto.CardsTotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.GoalkeeperRankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchResultResponse;
import co.edu.escuelaing.techcup.statistics.dto.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.dto.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.PlayerCardsResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingResponse;
import co.edu.escuelaing.techcup.statistics.dto.RankingType;
import co.edu.escuelaing.techcup.statistics.dto.TeamAverageResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamGoalsResponse;
import co.edu.escuelaing.techcup.statistics.dto.TeamMatchRecordResponse;
import co.edu.escuelaing.techcup.statistics.dto.TotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentMatchAveragesResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentRecognitionResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentStandingsResponse;
import co.edu.escuelaing.techcup.statistics.entity.MatchResult;
import co.edu.escuelaing.techcup.statistics.entity.PlayerMatchStat;
import co.edu.escuelaing.techcup.statistics.entity.TournamentRecognition;
import co.edu.escuelaing.techcup.statistics.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.exception.RecognitionNotFoundException;
import co.edu.escuelaing.techcup.statistics.repository.PlayerMatchStatRepository;
import co.edu.escuelaing.techcup.statistics.repository.TournamentRecognitionRepository;

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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceImplTest {

    @Mock
    private PlayerMatchStatRepository repository;

    @Mock
    private TournamentClient tournamentClient;

    @Mock
    private TournamentRecognitionRepository recognitionRepository;

    private StatisticsService statisticsService;

    private static final Long PLAYER_ID = 1L;
    private static final Long TEAM_ID = 10L;
    private static final Long TOURNAMENT_ID = 100L;

    @BeforeEach
    void setUp() {
        statisticsService = new StatisticsServiceImpl(repository, recognitionRepository, tournamentClient);
    }

    private PlayerMatchStat stat(Long playerId, Long teamId, Long matchId, MatchResult result,
                                  int goals, int fouls, int minutes) {
        return PlayerMatchStat.builder()
                .playerId(playerId)
                .teamId(teamId)
                .matchId(matchId)
                .tournamentId(TOURNAMENT_ID)
                .result(result)
                .goals(goals)
                .foulsCommitted(fouls)
                .minutesPlayed(minutes)
                .build();
    }

    private PlayerMatchStat goalkeeperStat(Long playerId, Long teamId, Long matchId, MatchResult result) {
        return PlayerMatchStat.builder()
                .playerId(playerId)
                .teamId(teamId)
                .matchId(matchId)
                .tournamentId(TOURNAMENT_ID)
                .result(result)
                .goals(0)
                .goalkeeper(true)
                .build();
    }

    // ---------- registerMatchStat ----------

    @Test
    void registerMatchStat_deberiaGuardarCuandoNoEsDuplicado() {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, TEAM_ID, 500L, TOURNAMENT_ID, MatchResult.WON,
                2, 1, 0, 3, 90, 1, false);

        when(repository.existsByPlayerIdAndMatchId(PLAYER_ID, 500L)).thenReturn(false);

        statisticsService.registerMatchStat(request);

        ArgumentCaptor<PlayerMatchStat> captor = ArgumentCaptor.forClass(PlayerMatchStat.class);
        verify(repository).save(captor.capture());

        PlayerMatchStat saved = captor.getValue();
        assertThat(saved.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(saved.getMatchId()).isEqualTo(500L);
        assertThat(saved.getGoals()).isEqualTo(2);
        assertThat(saved.getAssists()).isEqualTo(1);
        assertThat(saved.isGoalkeeper()).isFalse();
        assertThat(saved.getResult()).isEqualTo(MatchResult.WON);
    }

    @Test
    void registerMatchStat_deberiaRellenarConCeroLosCamposNulos() {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, TEAM_ID, 501L, TOURNAMENT_ID, MatchResult.LOST,
                null, null, null, null, null, null, null);

        when(repository.existsByPlayerIdAndMatchId(PLAYER_ID, 501L)).thenReturn(false);

        statisticsService.registerMatchStat(request);

        ArgumentCaptor<PlayerMatchStat> captor = ArgumentCaptor.forClass(PlayerMatchStat.class);
        verify(repository).save(captor.capture());

        PlayerMatchStat saved = captor.getValue();
        assertThat(saved.getGoals()).isZero();
        assertThat(saved.getMinutesPlayed()).isZero();
        assertThat(saved.getAssists()).isZero();
        assertThat(saved.isGoalkeeper()).isFalse();
    }

    @Test
    void registerMatchStat_deberiaLanzarExcepcionSiYaExisteElPartidoParaElJugador() {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, TEAM_ID, 500L, TOURNAMENT_ID, MatchResult.WON,
                2, 0, 0, 1, 90, 0, false);

        when(repository.existsByPlayerIdAndMatchId(PLAYER_ID, 500L)).thenReturn(true);

        assertThrows(DuplicateMatchStatException.class,
                () -> statisticsService.registerMatchStat(request));

        verify(repository, never()).save(any());
    }

    // ---------- Promedios de jugador ----------

    @Test
    void getAverageGoals_deberiaCalcularElPromedioYRedondear() {
        List<PlayerMatchStat> stats = List.of(
                stat(PLAYER_ID, TEAM_ID, 1L, MatchResult.WON, 2, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 2L, MatchResult.LOST, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 3L, MatchResult.WON, 2, 0, 90));
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(stats);

        PlayerAverageResponse response = statisticsService.getAverageGoals(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.value()).isEqualTo(1.67);
        assertThat(response.matchesConsidered()).isEqualTo(3L);
    }

    @Test
    void getAverageFouls_deberiaCalcularElPromedio() {
        List<PlayerMatchStat> stats = List.of(
                stat(PLAYER_ID, TEAM_ID, 1L, MatchResult.WON, 0, 4, 90),
                stat(PLAYER_ID, TEAM_ID, 2L, MatchResult.LOST, 0, 2, 90));
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(stats);

        PlayerAverageResponse response = statisticsService.getAverageFouls(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.value()).isEqualTo(3.0);
        assertThat(response.metric()).isEqualTo("averageFouls");
    }

    @Test
    void getAverageMinutesPlayed_deberiaCalcularElPromedio() {
        List<PlayerMatchStat> stats = List.of(
                stat(PLAYER_ID, TEAM_ID, 1L, MatchResult.WON, 0, 0, 60),
                stat(PLAYER_ID, TEAM_ID, 2L, MatchResult.LOST, 0, 0, 90));
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(stats);

        PlayerAverageResponse response = statisticsService.getAverageMinutesPlayed(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.value()).isEqualTo(75.0);
    }

    @Test
    void getAverageWinRate_deberiaCalcularPorcentajeCorrectamente() {
        List<PlayerMatchStat> stats = List.of(
                stat(PLAYER_ID, TEAM_ID, 1L, MatchResult.WON, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 2L, MatchResult.WON, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 3L, MatchResult.WON, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 4L, MatchResult.LOST, 0, 0, 90));
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(stats);

        PlayerAverageResponse response = statisticsService.getAverageWinRate(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.value()).isEqualTo(75.0);
    }

    @Test
    void getAverageWinRate_deberiaDevolverCeroSiNoHaJugadoPartidos() {
        when(repository.findByPlayerId(PLAYER_ID)).thenReturn(List.of());

        PlayerAverageResponse response = statisticsService.getAverageWinRate(PLAYER_ID, null);

        assertThat(response.value()).isZero();
        assertThat(response.matchesConsidered()).isZero();
    }

    @Test
    void getMatchesPlayed_deberiaContarLosPartidosDelJugador() {
        when(repository.findByPlayerId(PLAYER_ID)).thenReturn(List.of(
                stat(PLAYER_ID, TEAM_ID, 1L, MatchResult.WON, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 2L, MatchResult.LOST, 0, 0, 90)));

        MatchesPlayedResponse response = statisticsService.getMatchesPlayed(PLAYER_ID, null);

        assertThat(response.matchesPlayed()).isEqualTo(2L);
    }

    // ---------- Totales y tarjetas de jugador ----------

    @Test
    void getPlayerTotalGoals_deberiaSumarTodosLosGoles() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(PLAYER_ID, TEAM_ID, 1L, MatchResult.WON, 2, 0, 90),
                stat(PLAYER_ID, TEAM_ID, 2L, MatchResult.WON, 3, 0, 90)));

        TotalResponse response = statisticsService.getPlayerTotalGoals(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.total()).isEqualTo(5L);
        assertThat(response.metric()).isEqualTo("totalGoals");
    }

    @Test
    void getPlayerTotalFouls_deberiaSumarTodasLasFaltas() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(PLAYER_ID, TEAM_ID, 1L, MatchResult.WON, 0, 2, 90),
                stat(PLAYER_ID, TEAM_ID, 2L, MatchResult.WON, 0, 3, 90)));

        TotalResponse response = statisticsService.getPlayerTotalFouls(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.total()).isEqualTo(5L);
    }

    @Test
    void getPlayerTotalAssists_deberiaSumarTodasLasAsistencias() {
        PlayerMatchStat withAssists = PlayerMatchStat.builder()
                .playerId(PLAYER_ID).teamId(TEAM_ID).matchId(1L).tournamentId(TOURNAMENT_ID)
                .result(MatchResult.WON).assists(2).build();
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID))
                .thenReturn(List.of(withAssists));

        TotalResponse response = statisticsService.getPlayerTotalAssists(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.total()).isEqualTo(2L);
        assertThat(response.metric()).isEqualTo("totalAssists");
    }

    @Test
    void getPlayerCards_deberiaSumarAmarillasYRojas() {
        PlayerMatchStat withCards = PlayerMatchStat.builder()
                .playerId(PLAYER_ID).teamId(TEAM_ID).matchId(1L).tournamentId(TOURNAMENT_ID)
                .result(MatchResult.WON).yellowCards(1).redCards(1).build();
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID))
                .thenReturn(List.of(withCards));

        PlayerCardsResponse response = statisticsService.getPlayerCards(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.yellowCards()).isEqualTo(1L);
        assertThat(response.redCards()).isEqualTo(1L);
    }

    // ---------- Rankings ----------

    @Test
    void getRanking_goles_deberiaOrdenarDescendente() {
        List<PlayerMatchStat> stats = List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 5, 0, 90),
                stat(2L, TEAM_ID, 1L, MatchResult.LOST, 2, 0, 90));
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(stats);

        RankingResponse response = statisticsService.getRanking(RankingType.GOALS, TOURNAMENT_ID, 10);

        assertThat(response.entries()).hasSize(2);
        assertThat(response.entries().get(0).playerId()).isEqualTo(1L);
        assertThat(response.entries().get(0).value()).isEqualTo(5L);
        assertThat(response.entries().get(0).position()).isEqualTo(1);
    }

    @Test
    void getRanking_faltas_deberiaOrdenarAscendente() {
        List<PlayerMatchStat> stats = List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 0, 5, 90),
                stat(2L, TEAM_ID, 1L, MatchResult.LOST, 0, 1, 90));
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(stats);

        RankingResponse response = statisticsService.getRanking(RankingType.FOULS, TOURNAMENT_ID, 10);

        assertThat(response.entries().get(0).playerId()).isEqualTo(2L);
        assertThat(response.entries().get(0).value()).isEqualTo(1L);
    }

    @Test
    void getRanking_historico_deberiaUsarFindAllCuandoNoHayTorneo() {
        when(repository.findAll()).thenReturn(List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 3, 0, 90)));

        RankingResponse response = statisticsService.getRanking(RankingType.GOALS, null, 10);

        assertThat(response.entries()).hasSize(1);
    }

    @Test
    void getGoalkeeperRanking_deberiaOrdenarPorMenosGolesRecibidos() {
        PlayerMatchStat keeper1 = goalkeeperStat(30L, 70L, 400L, MatchResult.LOST);
        PlayerMatchStat opponent1 = stat(31L, 71L, 400L, MatchResult.WON, 2, 0, 90);
        PlayerMatchStat keeper2 = goalkeeperStat(32L, 72L, 401L, MatchResult.WON);
        PlayerMatchStat opponent2 = stat(33L, 73L, 401L, MatchResult.LOST, 0, 0, 90);

        when(repository.findByTournamentId(TOURNAMENT_ID))
                .thenReturn(List.of(keeper1, opponent1, keeper2, opponent2));

        GoalkeeperRankingResponse response = statisticsService.getGoalkeeperRanking(TOURNAMENT_ID, 10);

        assertThat(response.entries()).hasSize(2);
        assertThat(response.entries().get(0).playerId()).isEqualTo(32L);
        assertThat(response.entries().get(0).goalsConceded()).isZero();
        assertThat(response.entries().get(1).playerId()).isEqualTo(30L);
        assertThat(response.entries().get(1).goalsConceded()).isEqualTo(2L);
    }

    // ---------- Estadísticas de equipo ----------

    @Test
    void getTeamStatisticsInActiveTournament_deberiaResolverElTorneoActivo() {
        when(tournamentClient.getActiveTournamentId()).thenReturn(TOURNAMENT_ID);
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 2, 0, 90),
                stat(2L, TEAM_ID, 1L, MatchResult.WON, 2, 0, 90)));
        when(repository.findByMatchId(1L)).thenReturn(List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 2, 0, 90),
                stat(2L, TEAM_ID, 1L, MatchResult.WON, 2, 0, 90),
                stat(9L, 99L, 1L, MatchResult.LOST, 1, 0, 90)));

        var response = statisticsService.getTeamStatisticsInActiveTournament(TEAM_ID);

        assertThat(response.matchesPlayed()).isEqualTo(1L);
        assertThat(response.wins()).isEqualTo(1L);
        assertThat(response.goalsFor()).isEqualTo(4L);
        assertThat(response.goalsAgainst()).isEqualTo(1L);
        assertThat(response.points()).isEqualTo(3L);
    }

    @Test
    void getTeamGoals_deberiaCalcularFavorContraYDiferencia() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 3, 0, 90)));
        when(repository.findByMatchId(1L)).thenReturn(List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 3, 0, 90),
                stat(2L, 99L, 1L, MatchResult.LOST, 1, 0, 90)));

        TeamGoalsResponse response = statisticsService.getTeamGoals(TEAM_ID, TOURNAMENT_ID);

        assertThat(response.goalsFor()).isEqualTo(3L);
        assertThat(response.goalsAgainst()).isEqualTo(1L);
        assertThat(response.goalDifference()).isEqualTo(2L);
    }

    @Test
    void getTeamMatchRecord_deberiaCalcularPorcentajes() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 1, 0, 90),
                stat(2L, TEAM_ID, 2L, MatchResult.WON, 1, 0, 90),
                stat(3L, TEAM_ID, 3L, MatchResult.DRAWN, 0, 0, 90),
                stat(4L, TEAM_ID, 4L, MatchResult.LOST, 0, 0, 90)));

        TeamMatchRecordResponse response = statisticsService.getTeamMatchRecord(TEAM_ID, TOURNAMENT_ID);

        assertThat(response.matchesPlayed()).isEqualTo(4L);
        assertThat(response.wins()).isEqualTo(2L);
        assertThat(response.winRatePercentage()).isEqualTo(50.0);
        assertThat(response.drawRatePercentage()).isEqualTo(25.0);
        assertThat(response.lossRatePercentage()).isEqualTo(25.0);
    }

    @Test
    void getTeamAverageGoals_deberiaPromediarPorPartidoNoPorFila() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 2, 0, 90),
                stat(2L, TEAM_ID, 2L, MatchResult.WON, 4, 0, 90)));

        TeamAverageResponse response = statisticsService.getTeamAverageGoals(TEAM_ID, TOURNAMENT_ID);

        assertThat(response.value()).isEqualTo(3.0);
        assertThat(response.matchesConsidered()).isEqualTo(2L);
    }

    @Test
    void getTeamAverageFouls_deberiaPromediarPorPartido() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 0, 3, 90),
                stat(2L, TEAM_ID, 2L, MatchResult.WON, 0, 5, 90)));

        TeamAverageResponse response = statisticsService.getTeamAverageFouls(TEAM_ID, TOURNAMENT_ID);

        assertThat(response.value()).isEqualTo(4.0);
    }

    @Test
    void getTeamTotalFouls_deberiaSumarTodasLasFaltasDelEquipo() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 0, 3, 90),
                stat(2L, TEAM_ID, 1L, MatchResult.WON, 0, 2, 90)));

        TotalResponse response = statisticsService.getTeamTotalFouls(TEAM_ID, TOURNAMENT_ID);

        assertThat(response.total()).isEqualTo(5L);
    }

    // ---------- Torneo: standings, promedios, tarjetas ----------

    @Test
    void getTournamentStandings_deberiaOrdenarPorPuntosDescendente() {
        PlayerMatchStat teamAPlayer = stat(20L, 60L, 300L, MatchResult.WON, 3, 0, 90);
        PlayerMatchStat teamBPlayer = stat(21L, 61L, 300L, MatchResult.LOST, 1, 0, 90);

        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(teamAPlayer, teamBPlayer));
        when(repository.findByTeamIdAndTournamentId(60L, TOURNAMENT_ID)).thenReturn(List.of(teamAPlayer));
        when(repository.findByTeamIdAndTournamentId(61L, TOURNAMENT_ID)).thenReturn(List.of(teamBPlayer));
        when(repository.findByMatchId(300L)).thenReturn(List.of(teamAPlayer, teamBPlayer));

        TournamentStandingsResponse response = statisticsService.getTournamentStandings(TOURNAMENT_ID);

        assertThat(response.standings()).hasSize(2);
        assertThat(response.standings().get(0).teamId()).isEqualTo(60L);
        assertThat(response.standings().get(0).points()).isEqualTo(3L);
        assertThat(response.standings().get(1).teamId()).isEqualTo(61L);
        assertThat(response.standings().get(1).points()).isEqualTo(0L);
    }

    @Test
    void getTournamentMatchAverages_deberiaPromediarSobreTodosLosPartidos() {
        List<PlayerMatchStat> stats = List.of(
                stat(1L, TEAM_ID, 1L, MatchResult.WON, 2, 4, 90),
                stat(2L, 99L, 1L, MatchResult.LOST, 1, 2, 90),
                stat(3L, TEAM_ID, 2L, MatchResult.WON, 3, 3, 90));
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(stats);

        TournamentMatchAveragesResponse response = statisticsService.getTournamentMatchAverages(TOURNAMENT_ID);

        assertThat(response.matchesConsidered()).isEqualTo(2L);
        assertThat(response.averageGoalsPerMatch()).isEqualTo(3.0);
        assertThat(response.averageFoulsPerMatch()).isEqualTo(4.5);
    }

    @Test
    void getTournamentMatchAverages_deberiaDevolverCeroSiNoHayPartidos() {
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of());

        TournamentMatchAveragesResponse response = statisticsService.getTournamentMatchAverages(TOURNAMENT_ID);

        assertThat(response.matchesConsidered()).isZero();
        assertThat(response.averageGoalsPerMatch()).isZero();
    }

    @Test
    void getTournamentCardsTotal_deberiaSumarTodasLasTarjetasDelTorneo() {
        PlayerMatchStat withCards = PlayerMatchStat.builder()
                .playerId(1L).teamId(TEAM_ID).matchId(1L).tournamentId(TOURNAMENT_ID)
                .result(MatchResult.WON).yellowCards(2).redCards(1).build();
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(withCards));

        CardsTotalResponse response = statisticsService.getTournamentCardsTotal(TOURNAMENT_ID);

        assertThat(response.scope()).isEqualTo("tournament");
        assertThat(response.yellowCards()).isEqualTo(2L);
        assertThat(response.redCards()).isEqualTo(1L);
    }

    // ---------- Partido ----------

    @Test
    void getMatchCardsTotal_deberiaSumarLasTarjetasDeEsePartido() {
        PlayerMatchStat withCards = PlayerMatchStat.builder()
                .playerId(1L).teamId(TEAM_ID).matchId(500L).tournamentId(TOURNAMENT_ID)
                .result(MatchResult.WON).yellowCards(1).redCards(0).build();
        when(repository.findByMatchId(500L)).thenReturn(List.of(withCards));

        CardsTotalResponse response = statisticsService.getMatchCardsTotal(500L);

        assertThat(response.scope()).isEqualTo("match");
        assertThat(response.id()).isEqualTo(500L);
        assertThat(response.yellowCards()).isEqualTo(1L);
    }

    @Test
    void getMatchResult_deberiaDevolverElResultadoDeCadaEquipo() {
        PlayerMatchStat teamAPlayer = stat(1L, 60L, 300L, MatchResult.WON, 2, 0, 90);
        PlayerMatchStat teamBPlayer = stat(2L, 61L, 300L, MatchResult.LOST, 0, 0, 90);
        when(repository.findByMatchId(300L)).thenReturn(List.of(teamAPlayer, teamBPlayer));

        MatchResultResponse response = statisticsService.getMatchResult(300L);

        assertThat(response.matchId()).isEqualTo(300L);
        assertThat(response.tournamentId()).isEqualTo(TOURNAMENT_ID);
        assertThat(response.teamResults()).hasSize(2);
    }

    // ---------- Reconocimientos ----------

    @Test
    void generateTournamentRecognitions_deberiaPublicarTodosLosGoleadoresEmpatados() {
        PlayerMatchStat scorer1 = stat(20L, 60L, 300L, MatchResult.WON, 3, 0, 90);
        PlayerMatchStat scorer2 = stat(22L, 62L, 301L, MatchResult.WON, 3, 0, 90);
        PlayerMatchStat other1 = stat(21L, 61L, 300L, MatchResult.LOST, 0, 0, 90);
        PlayerMatchStat other2 = stat(23L, 63L, 301L, MatchResult.LOST, 0, 0, 90);

        when(repository.findByTournamentId(TOURNAMENT_ID))
                .thenReturn(List.of(scorer1, other1, scorer2, other2));
        when(repository.findByTeamIdAndTournamentId(60L, TOURNAMENT_ID)).thenReturn(List.of(scorer1));
        when(repository.findByTeamIdAndTournamentId(61L, TOURNAMENT_ID)).thenReturn(List.of(other1));
        when(repository.findByTeamIdAndTournamentId(62L, TOURNAMENT_ID)).thenReturn(List.of(scorer2));
        when(repository.findByTeamIdAndTournamentId(63L, TOURNAMENT_ID)).thenReturn(List.of(other2));
        when(repository.findByMatchId(300L)).thenReturn(List.of(scorer1, other1));
        when(repository.findByMatchId(301L)).thenReturn(List.of(scorer2, other2));

        when(recognitionRepository.findByTournamentId(TOURNAMENT_ID)).thenReturn(Optional.empty());
        when(recognitionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TournamentRecognitionResponse response = statisticsService.generateTournamentRecognitions(TOURNAMENT_ID);

        assertThat(response.topScorersGoals()).isEqualTo(3L);
        assertThat(response.topScorers()).extracting(TournamentRecognitionResponse.PlayerGoals::playerId)
                .containsExactlyInAnyOrder(20L, 22L);
        verify(recognitionRepository).save(any(TournamentRecognition.class));
    }

    @Test
    void getTournamentRecognitions_deberiaLeerLoYaGuardado() {
        TournamentRecognition saved = TournamentRecognition.builder()
                .tournamentId(TOURNAMENT_ID)
                .topScorerPlayerIds(List.of(20L))
                .topScorersGoals(3)
                .bestDefenseTeamIds(List.of(60L))
                .bestDefenseGoalsAgainst(0)
                .build();
        when(recognitionRepository.findByTournamentId(TOURNAMENT_ID)).thenReturn(Optional.of(saved));

        TournamentRecognitionResponse response = statisticsService.getTournamentRecognitions(TOURNAMENT_ID);

        assertThat(response.topScorers()).hasSize(1);
        assertThat(response.topScorers().get(0).playerId()).isEqualTo(20L);
    }

    @Test
    void getTournamentRecognitions_deberiaLanzarExcepcionSiNoSeHaGenerado() {
        when(recognitionRepository.findByTournamentId(TOURNAMENT_ID)).thenReturn(Optional.empty());

        assertThrows(RecognitionNotFoundException.class,
                () -> statisticsService.getTournamentRecognitions(TOURNAMENT_ID));
    }
}

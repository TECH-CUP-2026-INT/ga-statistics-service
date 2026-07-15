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
class StatisticsUseCaseImplTest {

    @Mock
    private PlayerMatchStatRepositoryPort repository;

    @Mock
    private TournamentClientPort tournamentClient;

    @Mock
    private TournamentRecognitionRepositoryPort recognitionRepository;

    private StatisticsUseCase statisticsUseCase;

    private static final String PLAYER_ID = "player-1";
    private static final String TEAM_ID = "team-10";
    private static final String TOURNAMENT_ID = "tournament-100";

    @BeforeEach
    void setUp() {
        statisticsUseCase = new StatisticsUseCaseImpl(repository, recognitionRepository, tournamentClient);
    }

    private PlayerMatchStatistic stat(String playerId, String teamId, String matchId, MatchResult result,
                                       int goals, int fouls, int minutes) {
        return PlayerMatchStatistic.builder()
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

    private PlayerMatchStatistic goalkeeperStat(String playerId, String teamId, String matchId, MatchResult result) {
        return PlayerMatchStatistic.builder()
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
        PlayerMatchStatistic statistic = PlayerMatchStatistic.builder()
                .playerId(PLAYER_ID).teamId(TEAM_ID).matchId("match-500").tournamentId(TOURNAMENT_ID)
                .result(MatchResult.WON).goals(2).assists(1).goalkeeper(false).build();

        when(repository.existsByPlayerIdAndMatchId(PLAYER_ID, "match-500")).thenReturn(false);

        statisticsUseCase.registerMatchStat(statistic);

        ArgumentCaptor<PlayerMatchStatistic> captor = ArgumentCaptor.forClass(PlayerMatchStatistic.class);
        verify(repository).save(captor.capture());

        PlayerMatchStatistic saved = captor.getValue();
        assertThat(saved.getPlayerId()).isEqualTo(PLAYER_ID);
        assertThat(saved.getGoals()).isEqualTo(2);
        assertThat(saved.getRegisteredAt()).isNotNull();
    }

    @Test
    void registerMatchStat_deberiaLanzarExcepcionSiYaExisteElPartidoParaElJugador() {
        PlayerMatchStatistic statistic = PlayerMatchStatistic.builder()
                .playerId(PLAYER_ID).teamId(TEAM_ID).matchId("match-500").tournamentId(TOURNAMENT_ID)
                .result(MatchResult.WON).goals(2).build();

        when(repository.existsByPlayerIdAndMatchId(PLAYER_ID, "match-500")).thenReturn(true);

        assertThrows(DuplicateMatchStatException.class,
                () -> statisticsUseCase.registerMatchStat(statistic));

        verify(repository, never()).save(any());
    }

    // ---------- Promedios de jugador ----------

    @Test
    void getAverageGoals_deberiaCalcularElPromedioYRedondear() {
        List<PlayerMatchStatistic> stats = List.of(
                stat(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m2", MatchResult.LOST, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m3", MatchResult.WON, 2, 0, 90));
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(stats);

        PlayerAverageResult response = statisticsUseCase.getAverageGoals(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.value()).isEqualTo(1.67);
        assertThat(response.matchesConsidered()).isEqualTo(3L);
    }

    @Test
    void getAverageWinRate_deberiaCalcularPorcentajeCorrectamente() {
        List<PlayerMatchStatistic> stats = List.of(
                stat(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m2", MatchResult.WON, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m3", MatchResult.WON, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m4", MatchResult.LOST, 0, 0, 90));
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(stats);

        PlayerAverageResult response = statisticsUseCase.getAverageWinRate(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.value()).isEqualTo(75.0);
    }

    @Test
    void getAverageWinRate_deberiaDevolverCeroSiNoHaJugadoPartidos() {
        when(repository.findByPlayerId(PLAYER_ID)).thenReturn(List.of());

        PlayerAverageResult response = statisticsUseCase.getAverageWinRate(PLAYER_ID, null);

        assertThat(response.value()).isZero();
        assertThat(response.matchesConsidered()).isZero();
    }

    @Test
    void getMatchesPlayed_deberiaContarLosPartidosDelJugador() {
        when(repository.findByPlayerId(PLAYER_ID)).thenReturn(List.of(
                stat(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 1, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m2", MatchResult.LOST, 0, 0, 90)));

        MatchesPlayedResult response = statisticsUseCase.getMatchesPlayed(PLAYER_ID, null);

        assertThat(response.matchesPlayed()).isEqualTo(2L);
    }

    // ---------- Totales y tarjetas de jugador ----------

    @Test
    void getPlayerTotalGoals_deberiaSumarTodosLosGoles() {
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat(PLAYER_ID, TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat(PLAYER_ID, TEAM_ID, "m2", MatchResult.WON, 3, 0, 90)));

        TotalResult response = statisticsUseCase.getPlayerTotalGoals(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.total()).isEqualTo(5L);
    }

    @Test
    void getPlayerTotalAssists_deberiaSumarTodasLasAsistencias() {
        PlayerMatchStatistic withAssists = PlayerMatchStatistic.builder()
                .playerId(PLAYER_ID).teamId(TEAM_ID).matchId("m1").tournamentId(TOURNAMENT_ID)
                .result(MatchResult.WON).assists(2).build();
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID))
                .thenReturn(List.of(withAssists));

        TotalResult response = statisticsUseCase.getPlayerTotalAssists(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.total()).isEqualTo(2L);
    }

    @Test
    void getPlayerCards_deberiaSumarAmarillasYRojas() {
        PlayerMatchStatistic withCards = PlayerMatchStatistic.builder()
                .playerId(PLAYER_ID).teamId(TEAM_ID).matchId("m1").tournamentId(TOURNAMENT_ID)
                .result(MatchResult.WON).yellowCards(1).redCards(1).build();
        when(repository.findByPlayerIdAndTournamentId(PLAYER_ID, TOURNAMENT_ID))
                .thenReturn(List.of(withCards));

        PlayerCardsResult response = statisticsUseCase.getPlayerCards(PLAYER_ID, TOURNAMENT_ID);

        assertThat(response.yellowCards()).isEqualTo(1L);
        assertThat(response.redCards()).isEqualTo(1L);
    }

    // ---------- Rankings ----------

    @Test
    void getRanking_goles_deberiaOrdenarDescendente() {
        List<PlayerMatchStatistic> stats = List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 5, 0, 90),
                stat("p2", TEAM_ID, "m1", MatchResult.LOST, 2, 0, 90));
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(stats);

        RankingResult response = statisticsUseCase.getRanking(RankingType.GOALS, TOURNAMENT_ID, 10);

        assertThat(response.entries()).hasSize(2);
        assertThat(response.entries().get(0).playerId()).isEqualTo("p1");
        assertThat(response.entries().get(0).value()).isEqualTo(5L);
    }

    @Test
    void getRanking_faltas_deberiaOrdenarAscendente() {
        List<PlayerMatchStatistic> stats = List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 0, 5, 90),
                stat("p2", TEAM_ID, "m1", MatchResult.LOST, 0, 1, 90));
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(stats);

        RankingResult response = statisticsUseCase.getRanking(RankingType.FOULS, TOURNAMENT_ID, 10);

        assertThat(response.entries().get(0).playerId()).isEqualTo("p2");
    }

    @Test
    void getRanking_historico_deberiaUsarFindAllCuandoNoHayTorneo() {
        when(repository.findAll()).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 3, 0, 90)));

        RankingResult response = statisticsUseCase.getRanking(RankingType.GOALS, null, 10);

        assertThat(response.entries()).hasSize(1);
    }

    @Test
    void getGoalkeeperRanking_deberiaOrdenarPorMenosGolesRecibidos() {
        PlayerMatchStatistic keeper1 = goalkeeperStat("gk1", "teamA", "m400", MatchResult.LOST);
        PlayerMatchStatistic opponent1 = stat("p31", "teamB", "m400", MatchResult.WON, 2, 0, 90);
        PlayerMatchStatistic keeper2 = goalkeeperStat("gk2", "teamC", "m401", MatchResult.WON);
        PlayerMatchStatistic opponent2 = stat("p33", "teamD", "m401", MatchResult.LOST, 0, 0, 90);

        when(repository.findByTournamentId(TOURNAMENT_ID))
                .thenReturn(List.of(keeper1, opponent1, keeper2, opponent2));

        GoalkeeperRankingResult response = statisticsUseCase.getGoalkeeperRanking(TOURNAMENT_ID, 10);

        assertThat(response.entries()).hasSize(2);
        assertThat(response.entries().get(0).playerId()).isEqualTo("gk2");
        assertThat(response.entries().get(1).playerId()).isEqualTo("gk1");
    }

    // ---------- Estadísticas de equipo ----------

    @Test
    void getTeamStatisticsInActiveTournament_deberiaResolverElTorneoActivo() {
        when(tournamentClient.getActiveTournamentId()).thenReturn(TOURNAMENT_ID);
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat("p2", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90)));
        when(repository.findByMatchId("m1")).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat("p2", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat("p9", "team99", "m1", MatchResult.LOST, 1, 0, 90)));

        var response = statisticsUseCase.getTeamStatisticsInActiveTournament(TEAM_ID);

        assertThat(response.matchesPlayed()).isEqualTo(1L);
        assertThat(response.wins()).isEqualTo(1L);
        assertThat(response.goalsFor()).isEqualTo(4L);
        assertThat(response.goalsAgainst()).isEqualTo(1L);
        assertThat(response.points()).isEqualTo(3L);
    }

    @Test
    void getTeamGoals_deberiaCalcularFavorContraYDiferencia() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 3, 0, 90)));
        when(repository.findByMatchId("m1")).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 3, 0, 90),
                stat("p2", "team99", "m1", MatchResult.LOST, 1, 0, 90)));

        TeamGoalsResult response = statisticsUseCase.getTeamGoals(TEAM_ID, TOURNAMENT_ID);

        assertThat(response.goalsFor()).isEqualTo(3L);
        assertThat(response.goalsAgainst()).isEqualTo(1L);
        assertThat(response.goalDifference()).isEqualTo(2L);
    }

    @Test
    void getTeamMatchRecord_deberiaCalcularPorcentajes() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 1, 0, 90),
                stat("p1", TEAM_ID, "m2", MatchResult.WON, 1, 0, 90),
                stat("p1", TEAM_ID, "m3", MatchResult.DRAWN, 0, 0, 90),
                stat("p1", TEAM_ID, "m4", MatchResult.LOST, 0, 0, 90)));

        TeamMatchRecordResult response = statisticsUseCase.getTeamMatchRecord(TEAM_ID, TOURNAMENT_ID);

        assertThat(response.matchesPlayed()).isEqualTo(4L);
        assertThat(response.winRatePercentage()).isEqualTo(50.0);
    }

    @Test
    void getTeamAverageGoals_deberiaPromediarPorPartidoNoPorFila() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat("p1", TEAM_ID, "m2", MatchResult.WON, 4, 0, 90)));

        TeamAverageResult response = statisticsUseCase.getTeamAverageGoals(TEAM_ID, TOURNAMENT_ID);

        assertThat(response.value()).isEqualTo(3.0);
    }

    @Test
    void getTeamTotalFouls_deberiaSumarTodasLasFaltasDelEquipo() {
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 0, 3, 90),
                stat("p2", TEAM_ID, "m1", MatchResult.WON, 0, 2, 90)));

        TotalResult response = statisticsUseCase.getTeamTotalFouls(TEAM_ID, TOURNAMENT_ID);

        assertThat(response.total()).isEqualTo(5L);
    }

    // ---------- Torneo ----------

    @Test
    void getTournamentStandings_deberiaOrdenarPorPuntosDescendente() {
        PlayerMatchStatistic teamAPlayer = stat("p20", "teamA", "m300", MatchResult.WON, 3, 0, 90);
        PlayerMatchStatistic teamBPlayer = stat("p21", "teamB", "m300", MatchResult.LOST, 1, 0, 90);

        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(teamAPlayer, teamBPlayer));
        when(repository.findByTeamIdAndTournamentId("teamA", TOURNAMENT_ID)).thenReturn(List.of(teamAPlayer));
        when(repository.findByTeamIdAndTournamentId("teamB", TOURNAMENT_ID)).thenReturn(List.of(teamBPlayer));
        when(repository.findByMatchId("m300")).thenReturn(List.of(teamAPlayer, teamBPlayer));

        TournamentStandingsResult response = statisticsUseCase.getTournamentStandings(TOURNAMENT_ID);

        assertThat(response.standings()).hasSize(2);
        assertThat(response.standings().get(0).teamId()).isEqualTo("teamA");
        assertThat(response.standings().get(0).points()).isEqualTo(3L);
    }

    @Test
    void getTournamentMatchAverages_deberiaPromediarSobreTodosLosPartidos() {
        List<PlayerMatchStatistic> stats = List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 2, 4, 90),
                stat("p2", "team99", "m1", MatchResult.LOST, 1, 2, 90),
                stat("p1", TEAM_ID, "m2", MatchResult.WON, 3, 3, 90));
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(stats);

        TournamentMatchAveragesResult response = statisticsUseCase.getTournamentMatchAverages(TOURNAMENT_ID);

        assertThat(response.matchesConsidered()).isEqualTo(2L);
        assertThat(response.averageGoalsPerMatch()).isEqualTo(3.0);
    }

    @Test
    void getTournamentCardsTotal_deberiaSumarTodasLasTarjetasDelTorneo() {
        PlayerMatchStatistic withCards = PlayerMatchStatistic.builder()
                .playerId("p1").teamId(TEAM_ID).matchId("m1").tournamentId(TOURNAMENT_ID)
                .result(MatchResult.WON).yellowCards(2).redCards(1).build();
        when(repository.findByTournamentId(TOURNAMENT_ID)).thenReturn(List.of(withCards));

        CardsTotalResult response = statisticsUseCase.getTournamentCardsTotal(TOURNAMENT_ID);

        assertThat(response.yellowCards()).isEqualTo(2L);
        assertThat(response.redCards()).isEqualTo(1L);
    }

    // ---------- Partido ----------

    @Test
    void getMatchCardsTotal_deberiaSumarLasTarjetasDeEsePartido() {
        PlayerMatchStatistic withCards = PlayerMatchStatistic.builder()
                .playerId("p1").teamId(TEAM_ID).matchId("m500").tournamentId(TOURNAMENT_ID)
                .result(MatchResult.WON).yellowCards(1).redCards(0).build();
        when(repository.findByMatchId("m500")).thenReturn(List.of(withCards));

        CardsTotalResult response = statisticsUseCase.getMatchCardsTotal("m500");

        assertThat(response.yellowCards()).isEqualTo(1L);
    }

    @Test
    void getMatchResult_deberiaDevolverElResultadoDeCadaEquipo() {
        PlayerMatchStatistic teamAPlayer = stat("p1", "teamA", "m300", MatchResult.WON, 2, 0, 90);
        PlayerMatchStatistic teamBPlayer = stat("p2", "teamB", "m300", MatchResult.LOST, 0, 0, 90);
        when(repository.findByMatchId("m300")).thenReturn(List.of(teamAPlayer, teamBPlayer));

        MatchResultResult response = statisticsUseCase.getMatchResult("m300");

        assertThat(response.teamResults()).hasSize(2);
    }

    // ---------- Reconocimientos ----------

    @Test
    void generateTournamentRecognitions_deberiaPublicarTodosLosGoleadoresEmpatados() {
        PlayerMatchStatistic scorer1 = stat("p20", "teamA", "m300", MatchResult.WON, 3, 0, 90);
        PlayerMatchStatistic scorer2 = stat("p22", "teamC", "m301", MatchResult.WON, 3, 0, 90);
        PlayerMatchStatistic other1 = stat("p21", "teamB", "m300", MatchResult.LOST, 0, 0, 90);
        PlayerMatchStatistic other2 = stat("p23", "teamD", "m301", MatchResult.LOST, 0, 0, 90);

        when(repository.findByTournamentId(TOURNAMENT_ID))
                .thenReturn(List.of(scorer1, other1, scorer2, other2));
        when(repository.findByTeamIdAndTournamentId("teamA", TOURNAMENT_ID)).thenReturn(List.of(scorer1));
        when(repository.findByTeamIdAndTournamentId("teamB", TOURNAMENT_ID)).thenReturn(List.of(other1));
        when(repository.findByTeamIdAndTournamentId("teamC", TOURNAMENT_ID)).thenReturn(List.of(scorer2));
        when(repository.findByTeamIdAndTournamentId("teamD", TOURNAMENT_ID)).thenReturn(List.of(other2));
        when(repository.findByMatchId("m300")).thenReturn(List.of(scorer1, other1));
        when(repository.findByMatchId("m301")).thenReturn(List.of(scorer2, other2));

        when(recognitionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TournamentRecognitionRecord response = statisticsUseCase.generateTournamentRecognitions(TOURNAMENT_ID);

        assertThat(response.getTopScorersGoals()).isEqualTo(3L);
        assertThat(response.getTopScorerPlayerIds()).containsExactlyInAnyOrder("p20", "p22");
        verify(recognitionRepository).save(any(TournamentRecognitionRecord.class));
    }

    @Test
    void getTournamentRecognitions_deberiaLeerLoYaGuardado() {
        TournamentRecognitionRecord saved = TournamentRecognitionRecord.builder()
                .tournamentId(TOURNAMENT_ID)
                .topScorerPlayerIds(List.of("p20"))
                .topScorersGoals(3)
                .bestDefenseTeamIds(List.of("teamA"))
                .bestDefenseGoalsAgainst(0)
                .build();
        when(recognitionRepository.findByTournamentId(TOURNAMENT_ID)).thenReturn(Optional.of(saved));

        TournamentRecognitionRecord response = statisticsUseCase.getTournamentRecognitions(TOURNAMENT_ID);

        assertThat(response.getTopScorerPlayerIds()).containsExactly("p20");
    }

    @Test
    void getTournamentRecognitions_deberiaLanzarExcepcionSiNoSeHaGenerado() {
        when(recognitionRepository.findByTournamentId(TOURNAMENT_ID)).thenReturn(Optional.empty());

        assertThrows(RecognitionNotFoundException.class,
                () -> statisticsUseCase.getTournamentRecognitions(TOURNAMENT_ID));
    }
}

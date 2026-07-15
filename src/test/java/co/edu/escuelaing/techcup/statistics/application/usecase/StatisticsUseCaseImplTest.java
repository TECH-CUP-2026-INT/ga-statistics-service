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

    private PlayerMatchStatDocument goalkeeperStat(String playerId, String teamId, String matchId, MatchResult result) {
        return PlayerMatchStatDocument.builder()
                .playerId(playerId).teamId(teamId).matchId(matchId).tournamentId(TOURNAMENT_ID)
                .result(result).goalkeeper(true).build();
    }

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
    void getTeamStatisticsInActiveTournament_deberiaResolverElTorneoActivo() {
        when(tournamentClient.getActiveTournamentId()).thenReturn(TOURNAMENT_ID);
        when(repository.findByTeamIdAndTournamentId(TEAM_ID, TOURNAMENT_ID)).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat("p2", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90)));
        when(repository.findByMatchId("m1")).thenReturn(List.of(
                stat("p1", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat("p2", TEAM_ID, "m1", MatchResult.WON, 2, 0, 90),
                stat("p9", "team99", "m1", MatchResult.LOST, 1, 0, 90)));
        var response = statisticsService.getTeamStatisticsInActiveTournament(TEAM_ID);
        assertThat(response.points()).isEqualTo(3L);
    }

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
    void getTournamentRecognitions_deberiaLanzarExcepcionSiNoSeHaGenerado() {
        when(recognitionRepository.findByTournamentId(TOURNAMENT_ID)).thenReturn(Optional.empty());
        assertThrows(RecognitionNotFoundException.class,
                () -> statisticsService.getTournamentRecognitions(TOURNAMENT_ID));
    }
}

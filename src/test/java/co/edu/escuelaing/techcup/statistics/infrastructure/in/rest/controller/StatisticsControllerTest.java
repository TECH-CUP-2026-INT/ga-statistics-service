package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.request.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.application.mapper.MatchStatRequestMapperImpl;
import co.edu.escuelaing.techcup.statistics.application.mapper.StatisticsResponseMapperImpl;
import co.edu.escuelaing.techcup.statistics.domain.model.CardsTotalResult;
import co.edu.escuelaing.techcup.statistics.domain.model.GoalkeeperRankingResult;
import co.edu.escuelaing.techcup.statistics.domain.model.MatchResult;
import co.edu.escuelaing.techcup.statistics.domain.model.MatchResultResult;
import co.edu.escuelaing.techcup.statistics.domain.model.MatchesPlayedResult;
import co.edu.escuelaing.techcup.statistics.domain.model.PlayerAverageResult;
import co.edu.escuelaing.techcup.statistics.domain.model.PlayerCardsResult;
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
import co.edu.escuelaing.techcup.statistics.domain.exception.ExternalServiceException;
import co.edu.escuelaing.techcup.statistics.domain.exception.RecognitionNotFoundException;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.in.StatisticsUseCase;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StatisticsController.class)
@Import({MatchStatRequestMapperImpl.class, StatisticsResponseMapperImpl.class})
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StatisticsUseCase statisticsUseCase;

    private static final String PLAYER_ID = "player-1";
    private static final String TEAM_ID = "team-10";
    private static final String TOURNAMENT_ID = "tournament-100";
    private static final String MATCH_ID = "match-500";

    // ---------- Ingesta ----------

    @Test
    void registerMatchStat_deberiaDevolver201CuandoEsValido() throws Exception {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, TEAM_ID, MATCH_ID, TOURNAMENT_ID, MatchResult.WON,
                2, 1, 0, 3, 90, 1, false);

        mockMvc.perform(post("/api/v1/statistics/events")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void registerMatchStat_deberiaDevolver400CuandoFaltaUnCampoObligatorio() throws Exception {
        String invalidJson = "{\"teamId\": \"team-10\", \"matchId\": \"match-500\"}";

        mockMvc.perform(post("/api/v1/statistics/events")
                        .contentType("application/json")
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerMatchStat_deberiaDevolver409CuandoEsDuplicado() throws Exception {
        MatchStatEventRequest request = new MatchStatEventRequest(
                PLAYER_ID, TEAM_ID, MATCH_ID, TOURNAMENT_ID, MatchResult.WON,
                2, 0, 0, 1, 90, 0, false);

        doThrow(new DuplicateMatchStatException(PLAYER_ID, MATCH_ID))
                .when(statisticsUseCase).registerMatchStat(any());

        mockMvc.perform(post("/api/v1/statistics/events")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    // ---------- Jugador ----------

    @Test
    void getAverageWinRate_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getAverageWinRate(eq(PLAYER_ID), any()))
                .thenReturn(new PlayerAverageResult(PLAYER_ID, TOURNAMENT_ID, "averageWinRatePercentage", 50.0, 2));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-win-rate", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value(50.0));
    }

    @Test
    void getAverageGoals_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getAverageGoals(eq(PLAYER_ID), any()))
                .thenReturn(new PlayerAverageResult(PLAYER_ID, null, "averageGoals", 1.5, 2));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-goals", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.metric").value("averageGoals"));
    }

    @Test
    void getMatchesPlayed_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getMatchesPlayed(eq(PLAYER_ID), any()))
                .thenReturn(new MatchesPlayedResult(PLAYER_ID, null, 5));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/matches-played", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matchesPlayed").value(5));
    }

    @Test
    void getPlayerTotalGoals_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getPlayerTotalGoals(eq(PLAYER_ID), any()))
                .thenReturn(new TotalResult(PLAYER_ID, null, "totalGoals", 5, 3));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/total-goals", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(5));
    }

    @Test
    void getPlayerTotalAssists_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getPlayerTotalAssists(eq(PLAYER_ID), any()))
                .thenReturn(new TotalResult(PLAYER_ID, null, "totalAssists", 2, 3));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/assists", PLAYER_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getPlayerCards_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getPlayerCards(eq(PLAYER_ID), any()))
                .thenReturn(new PlayerCardsResult(PLAYER_ID, null, 2, 0));

        mockMvc.perform(get("/api/v1/statistics/players/{id}/cards", PLAYER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yellowCards").value(2));
    }

    // ---------- Rankings ----------

    @Test
    void getRanking_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getRanking(eq(RankingType.GOALS), any(), eq(10)))
                .thenReturn(new RankingResult("GOALS", null,
                        List.of(new RankingResult.RankingEntry(1, PLAYER_ID, 5))));

        mockMvc.perform(get("/api/v1/statistics/rankings").param("type", "GOALS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.entries[0].playerId").value(PLAYER_ID));
    }

    @Test
    void getGoalkeeperRanking_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getGoalkeeperRanking(any(), eq(10)))
                .thenReturn(new GoalkeeperRankingResult(TOURNAMENT_ID,
                        List.of(new GoalkeeperRankingResult.GoalkeeperEntry(1, PLAYER_ID, 0))));

        mockMvc.perform(get("/api/v1/statistics/goalkeeper-ranking").param("tournamentId", TOURNAMENT_ID))
                .andExpect(status().isOk());
    }

    // ---------- Equipo ----------

    @Test
    void getTeamStatisticsInActiveTournament_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTeamStatisticsInActiveTournament(TEAM_ID))
                .thenReturn(new TeamStatisticsResult(TEAM_ID, TOURNAMENT_ID, 2, 1, 0, 1, 3, 2, 1, 3));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/statistics", TEAM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.points").value(3));
    }

    @Test
    void getTeamStatisticsInActiveTournament_deberiaDevolver502SiTorneosNoResponde() throws Exception {
        when(statisticsUseCase.getTeamStatisticsInActiveTournament(TEAM_ID))
                .thenThrow(new ExternalServiceException("Torneos no disponible"));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/statistics", TEAM_ID))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getTeamMatchRecord_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTeamMatchRecord(eq(TEAM_ID), any()))
                .thenReturn(new TeamMatchRecordResult(TEAM_ID, null, 4, 2, 1, 1, 50.0, 25.0, 25.0));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/match-record", TEAM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.winRatePercentage").value(50.0));
    }

    @Test
    void getTeamAverageGoals_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTeamAverageGoals(eq(TEAM_ID), any()))
                .thenReturn(new TeamAverageResult(TEAM_ID, null, "averageGoalsPerMatch", 2.5, 2));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/average-goals", TEAM_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getTeamTotalFouls_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTeamTotalFouls(eq(TEAM_ID), any()))
                .thenReturn(new TotalResult(TEAM_ID, null, "totalFouls", 8, 2));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/total-fouls", TEAM_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getTeamGoals_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTeamGoals(eq(TEAM_ID), any()))
                .thenReturn(new TeamGoalsResult(TEAM_ID, null, 5, 2, 3));

        mockMvc.perform(get("/api/v1/statistics/teams/{id}/goals", TEAM_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.goalDifference").value(3));
    }

    // ---------- Torneo ----------

    @Test
    void getTournamentStandings_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTournamentStandings(TOURNAMENT_ID))
                .thenReturn(new TournamentStandingsResult(TOURNAMENT_ID, List.of(
                        new TeamStatisticsResult(TEAM_ID, TOURNAMENT_ID, 1, 1, 0, 0, 3, 0, 3, 3))));

        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/standings", TOURNAMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.standings[0].teamId").value(TEAM_ID));
    }

    @Test
    void getTournamentMatchAverages_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTournamentMatchAverages(TOURNAMENT_ID))
                .thenReturn(new TournamentMatchAveragesResult(TOURNAMENT_ID, 5, 2.8, 9.1, 3.2));

        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/match-averages", TOURNAMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageGoalsPerMatch").value(2.8));
    }

    @Test
    void getTournamentCardsTotal_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTournamentCardsTotal(TOURNAMENT_ID))
                .thenReturn(new CardsTotalResult("tournament", TOURNAMENT_ID, 10, 2));

        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/cards", TOURNAMENT_ID))
                .andExpect(status().isOk());
    }

    @Test
    void generateTournamentRecognitions_deberiaDevolver201() throws Exception {
        when(statisticsUseCase.generateTournamentRecognitions(TOURNAMENT_ID))
                .thenReturn(TournamentRecognitionRecord.builder()
                        .tournamentId(TOURNAMENT_ID)
                        .topScorerPlayerIds(List.of(PLAYER_ID))
                        .topScorersGoals(5)
                        .bestDefenseTeamIds(List.of(TEAM_ID))
                        .bestDefenseGoalsAgainst(0)
                        .generatedAt(LocalDateTime.now())
                        .build());

        mockMvc.perform(post("/api/v1/statistics/tournaments/{id}/recognitions", TOURNAMENT_ID))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.topScorersGoals").value(5));
    }

    @Test
    void getTournamentRecognitions_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTournamentRecognitions(TOURNAMENT_ID))
                .thenReturn(TournamentRecognitionRecord.builder()
                        .tournamentId(TOURNAMENT_ID)
                        .topScorerPlayerIds(List.of(PLAYER_ID))
                        .topScorersGoals(5)
                        .bestDefenseTeamIds(List.of(TEAM_ID))
                        .bestDefenseGoalsAgainst(0)
                        .generatedAt(LocalDateTime.now())
                        .build());

        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/recognitions", TOURNAMENT_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getTournamentRecognitions_deberiaDevolver404SiNoSeHaGenerado() throws Exception {
        when(statisticsUseCase.getTournamentRecognitions(TOURNAMENT_ID))
                .thenThrow(new RecognitionNotFoundException(TOURNAMENT_ID));

        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/recognitions", TOURNAMENT_ID))
                .andExpect(status().isNotFound());
    }

    // ---------- Partido ----------

    @Test
    void getMatchCardsTotal_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getMatchCardsTotal(MATCH_ID))
                .thenReturn(new CardsTotalResult("match", MATCH_ID, 1, 0));

        mockMvc.perform(get("/api/v1/statistics/matches/{id}/cards", MATCH_ID))
                .andExpect(status().isOk());
    }

    @Test
    void getMatchResult_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getMatchResult(MATCH_ID))
                .thenReturn(new MatchResultResult(MATCH_ID, TOURNAMENT_ID, List.of(
                        new MatchResultResult.TeamResultEntry(TEAM_ID, MatchResult.WON))));

        mockMvc.perform(get("/api/v1/statistics/matches/{id}/result", MATCH_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.teamResults[0].result").value("WON"));
    }
}

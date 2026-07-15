package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest;

import co.edu.escuelaing.techcup.statistics.application.mapper.PlayerMatchStatMapperImpl;
import co.edu.escuelaing.techcup.statistics.application.mapper.StatisticsResponseMapperImpl;
import co.edu.escuelaing.techcup.statistics.domain.model.*;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.in.StatisticsUseCase;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.controller.StatisticsController;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.request.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.domain.exception.DuplicateMatchStatException;
import co.edu.escuelaing.techcup.statistics.infrastructure.exception.ExternalServiceException;
import co.edu.escuelaing.techcup.statistics.domain.exception.RecognitionNotFoundException;

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
@Import({PlayerMatchStatMapperImpl.class, StatisticsResponseMapperImpl.class})
class StatisticsControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private StatisticsUseCase statisticsUseCase;

    private static final String PID = "player-1", TID = "team-10", TNID = "tournament-100", MID = "match-500";

    @Test
    void registerMatchStat_deberiaDevolver201() throws Exception {
        var req = new MatchStatEventRequest(PID, TID, MID, TNID, MatchResult.WON, 2, 1, 0, 3, 90, 1, false);
        mockMvc.perform(post("/api/v1/statistics/events").contentType("application/json")
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated());
    }

    @Test
    void registerMatchStat_deberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/v1/statistics/events").contentType("application/json")
                .content("{\"teamId\": \"t\"}")).andExpect(status().isBadRequest());
    }

    @Test
    void registerMatchStat_deberiaDevolver409() throws Exception {
        var req = new MatchStatEventRequest(PID, TID, MID, TNID, MatchResult.WON, 2, 0, 0, 1, 90, 0, false);
        doThrow(new DuplicateMatchStatException(PID, MID)).when(statisticsUseCase).registerMatchStat(any());
        mockMvc.perform(post("/api/v1/statistics/events").contentType("application/json")
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isConflict());
    }

    @Test
    void getAverageWinRate_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getAverageWinRate(eq(PID), any()))
                .thenReturn(new PlayerAverageResult(PID, TNID, "avg", 50.0, 2));
        mockMvc.perform(get("/api/v1/statistics/players/{id}/average-win-rate", PID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.value").value(50.0));
    }

    @Test
    void getTeamStatisticsInActiveTournament_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTeamStatisticsInActiveTournament(TID))
                .thenReturn(new TeamStatisticsResult(TID, TNID, 2, 1, 0, 1, 3, 2, 1, 3));
        mockMvc.perform(get("/api/v1/statistics/teams/{id}/statistics", TID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.points").value(3));
    }

    @Test
    void getTeamStatisticsInActiveTournament_deberiaDevolver502() throws Exception {
        when(statisticsUseCase.getTeamStatisticsInActiveTournament(TID))
                .thenThrow(new ExternalServiceException("error"));
        mockMvc.perform(get("/api/v1/statistics/teams/{id}/statistics", TID))
                .andExpect(status().isBadGateway());
    }

    @Test
    void getTournamentStandings_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getTournamentStandings(TNID))
                .thenReturn(new TournamentStandingsResult(TNID, List.of(
                        new TeamStatisticsResult(TID, TNID, 1, 1, 0, 0, 3, 0, 3, 3))));
        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/standings", TNID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.standings[0].teamId").value(TID));
    }

    @Test
    void getRanking_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getRanking(eq(RankingType.GOALS), any(), eq(10)))
                .thenReturn(new RankingResult("GOALS", null, List.of(new RankingResult.RankingEntry(1, PID, 5))));
        mockMvc.perform(get("/api/v1/statistics/rankings").param("type", "GOALS"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.entries[0].playerId").value(PID));
    }

    @Test
    void generateTournamentRecognitions_deberiaDevolver201() throws Exception {
        when(statisticsUseCase.generateTournamentRecognitions(TNID))
                .thenReturn(TournamentRecognitionRecord.builder().tournamentId(TNID)
                        .topScorerPlayerIds(List.of(PID)).topScorersGoals(5)
                        .bestDefenseTeamIds(List.of(TID)).bestDefenseGoalsAgainst(0)
                        .generatedAt(LocalDateTime.now()).build());
        mockMvc.perform(post("/api/v1/statistics/tournaments/{id}/recognitions", TNID))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.topScorersGoals").value(5));
    }

    @Test
    void getTournamentRecognitions_deberiaDevolver404() throws Exception {
        when(statisticsUseCase.getTournamentRecognitions(TNID))
                .thenThrow(new RecognitionNotFoundException(TNID));
        mockMvc.perform(get("/api/v1/statistics/tournaments/{id}/recognitions", TNID))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMatchResult_deberiaDevolver200() throws Exception {
        when(statisticsUseCase.getMatchResult(MID))
                .thenReturn(new MatchResultResult(MID, TNID, List.of(
                        new MatchResultResult.TeamResultEntry(TID, MatchResult.WON))));
        mockMvc.perform(get("/api/v1/statistics/matches/{id}/result", MID))
                .andExpect(status().isOk()).andExpect(jsonPath("$.teamResults[0].result").value("WON"));
    }
}

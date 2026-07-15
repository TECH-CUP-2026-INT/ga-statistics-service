package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.controller;

import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.request.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.CardsTotalResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.GoalkeeperRankingResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.MatchResultResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.MatchesPlayedResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.PlayerAverageResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.PlayerCardsResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.RankingResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TeamAverageResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TeamGoalsResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TeamMatchRecordResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TeamStatisticsResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TotalResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TournamentMatchAveragesResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TournamentRecognitionResponse;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.TournamentStandingsResponse;
import co.edu.escuelaing.techcup.statistics.application.mapper.MatchStatRequestMapper;
import co.edu.escuelaing.techcup.statistics.application.mapper.StatisticsResponseMapper;
import co.edu.escuelaing.techcup.statistics.domain.model.RankingType;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.in.StatisticsUseCase;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

/**
 * Adaptador de entrada (driving adapter): traduce HTTP hacia el caso de uso
 * y de vuelta. Es el UNICO lugar que conoce tanto los DTOs web como el
 * dominio -- el caso de uso (StatisticsUseCase) nunca ve un DTO.
 */
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Player, team, match and tournament statistics")
public class StatisticsController {

    private final StatisticsUseCase statisticsUseCase;
    private final MatchStatRequestMapper requestMapper;
    private final StatisticsResponseMapper responseMapper;

    // ---------- Ingestion ----------

    @Operation(
            summary = "Register a match event",
            description = "Internal endpoint consumed by the Competition service (live "
                    + "refereeing). Called once a match finishes, with the summary of ONE "
                    + "player in that match. For a walkover, submit the present team as WON "
                    + "and the absent team as LOST; there is no separate walkover value.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Event registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error in the request body"),
            @ApiResponse(responseCode = "409", description = "This player already has a stat "
                    + "recorded for this match")
    })
    @PostMapping("/events")
    public ResponseEntity<Void> registerMatchStat(@Valid @RequestBody MatchStatEventRequest request) {
        statisticsUseCase.registerMatchStat(requestMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ---------- Player ----------

    @Operation(summary = "Get a player's win rate")
    @GetMapping("/players/{playerId}/average-win-rate")
    public ResponseEntity<PlayerAverageResponse> getAverageWinRate(
            @PathVariable String playerId,
            @Parameter(description = "Optional tournament filter") @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getAverageWinRate(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's average goals per match")
    @GetMapping("/players/{playerId}/average-goals")
    public ResponseEntity<PlayerAverageResponse> getAverageGoals(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getAverageGoals(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's average fouls committed per match")
    @GetMapping("/players/{playerId}/average-fouls")
    public ResponseEntity<PlayerAverageResponse> getAverageFouls(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getAverageFouls(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's average minutes played per match")
    @GetMapping("/players/{playerId}/average-minutes-played")
    public ResponseEntity<PlayerAverageResponse> getAverageMinutesPlayed(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getAverageMinutesPlayed(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's total number of matches played")
    @GetMapping("/players/{playerId}/matches-played")
    public ResponseEntity<MatchesPlayedResponse> getMatchesPlayed(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getMatchesPlayed(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's total goals scored (sum, not average)")
    @GetMapping("/players/{playerId}/total-goals")
    public ResponseEntity<TotalResponse> getPlayerTotalGoals(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getPlayerTotalGoals(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's total fouls committed (sum, not average)")
    @GetMapping("/players/{playerId}/total-fouls")
    public ResponseEntity<TotalResponse> getPlayerTotalFouls(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getPlayerTotalFouls(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's total assists")
    @GetMapping("/players/{playerId}/assists")
    public ResponseEntity<TotalResponse> getPlayerTotalAssists(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getPlayerTotalAssists(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's accumulated yellow and red cards")
    @GetMapping("/players/{playerId}/cards")
    public ResponseEntity<PlayerCardsResponse> getPlayerCards(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getPlayerCards(playerId, tournamentId)));
    }

    // ---------- Team ----------

    @Operation(summary = "Get a team's full statistics in the currently active tournament")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "502", description = "The Tournaments service is unavailable")
    })
    @GetMapping("/teams/{teamId}/statistics")
    public ResponseEntity<TeamStatisticsResponse> getTeamStatisticsInActiveTournament(
            @PathVariable String teamId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamStatisticsInActiveTournament(teamId)));
    }

    @Operation(summary = "Get a team's match record (wins/draws/losses with percentages)")
    @GetMapping("/teams/{teamId}/match-record")
    public ResponseEntity<TeamMatchRecordResponse> getTeamMatchRecord(
            @PathVariable String teamId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamMatchRecord(teamId, tournamentId)));
    }

    @Operation(summary = "Get a team's average goals scored per match")
    @GetMapping("/teams/{teamId}/average-goals")
    public ResponseEntity<TeamAverageResponse> getTeamAverageGoals(
            @PathVariable String teamId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamAverageGoals(teamId, tournamentId)));
    }

    @Operation(summary = "Get a team's average fouls committed per match")
    @GetMapping("/teams/{teamId}/average-fouls")
    public ResponseEntity<TeamAverageResponse> getTeamAverageFouls(
            @PathVariable String teamId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamAverageFouls(teamId, tournamentId)));
    }

    @Operation(summary = "Get a team's total fouls committed (sum, not average)")
    @GetMapping("/teams/{teamId}/total-fouls")
    public ResponseEntity<TotalResponse> getTeamTotalFouls(
            @PathVariable String teamId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamTotalFouls(teamId, tournamentId)));
    }

    @Operation(summary = "Get a team's goals for, against, and goal difference")
    @GetMapping("/teams/{teamId}/goals")
    public ResponseEntity<TeamGoalsResponse> getTeamGoals(
            @PathVariable String teamId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTeamGoals(teamId, tournamentId)));
    }

    // ---------- Tournament ----------

    @Operation(summary = "Get the full tournament standings table")
    @GetMapping("/tournaments/{tournamentId}/standings")
    public ResponseEntity<TournamentStandingsResponse> getTournamentStandings(
            @PathVariable String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTournamentStandings(tournamentId)));
    }

    @Operation(summary = "Get the public player ranking")
    @GetMapping("/rankings")
    public ResponseEntity<RankingResponse> getRanking(
            @Parameter(description = "GOALS, WINS, FOULS or MINUTES") @RequestParam RankingType type,
            @RequestParam(required = false) String tournamentId,
            @Parameter(description = "Top N results to return") @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getRanking(type, tournamentId, limit)));
    }

    @Operation(summary = "Get the goalkeeper ranking (fewest goals conceded)")
    @GetMapping("/goalkeeper-ranking")
    public ResponseEntity<GoalkeeperRankingResponse> getGoalkeeperRanking(
            @RequestParam(required = false) String tournamentId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getGoalkeeperRanking(tournamentId, limit)));
    }

    @Operation(summary = "Get average goals, fouls and cards per match across the whole tournament")
    @GetMapping("/tournaments/{tournamentId}/match-averages")
    public ResponseEntity<TournamentMatchAveragesResponse> getTournamentMatchAverages(
            @PathVariable String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTournamentMatchAverages(tournamentId)));
    }

    @Operation(summary = "Get total yellow and red cards for the whole tournament")
    @GetMapping("/tournaments/{tournamentId}/cards")
    public ResponseEntity<CardsTotalResponse> getTournamentCardsTotal(
            @PathVariable String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTournamentCardsTotal(tournamentId)));
    }

    @Operation(summary = "Generate and persist the tournament recognitions")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recognition generated and saved")
    })
    @PostMapping("/tournaments/{tournamentId}/recognitions")
    public ResponseEntity<TournamentRecognitionResponse> generateTournamentRecognitions(
            @PathVariable String tournamentId) {
        TournamentRecognitionResponse response =
                responseMapper.toResponse(statisticsUseCase.generateTournamentRecognitions(tournamentId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get the previously saved tournament recognitions")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "No recognition has been generated yet")
    })
    @GetMapping("/tournaments/{tournamentId}/recognitions")
    public ResponseEntity<TournamentRecognitionResponse> getTournamentRecognitions(
            @PathVariable String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getTournamentRecognitions(tournamentId)));
    }

    // ---------- Match ----------

    @Operation(summary = "Get total yellow and red cards for a single match")
    @GetMapping("/matches/{matchId}/cards")
    public ResponseEntity<CardsTotalResponse> getMatchCardsTotal(@PathVariable String matchId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getMatchCardsTotal(matchId)));
    }

    @Operation(summary = "Get the result of a match for each team")
    @GetMapping("/matches/{matchId}/result")
    public ResponseEntity<MatchResultResponse> getMatchResult(@PathVariable String matchId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsUseCase.getMatchResult(matchId)));
    }
}

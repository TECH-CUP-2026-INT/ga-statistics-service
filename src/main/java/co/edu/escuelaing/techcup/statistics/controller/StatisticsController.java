package co.edu.escuelaing.techcup.statistics.controller;

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
import co.edu.escuelaing.techcup.statistics.dto.TeamStatisticsResponse;
import co.edu.escuelaing.techcup.statistics.dto.TotalResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentMatchAveragesResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentRecognitionResponse;
import co.edu.escuelaing.techcup.statistics.dto.TournamentStandingsResponse;
import co.edu.escuelaing.techcup.statistics.mapper.PlayerMatchStatMapper;
import co.edu.escuelaing.techcup.statistics.mapper.StatisticsResponseMapper;
import co.edu.escuelaing.techcup.statistics.service.StatisticsService;

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
 * IMPORTANTE (separacion de capas): este controller es el UNICO lugar que
 * conoce tanto los DTOs de la web como los objetos de dominio. Mapea
 * DTO de entrada -&gt; dominio ANTES de llamar al service, y dominio -&gt; DTO
 * de salida DESPUES de llamarlo. El service nunca ve un DTO.
 */
@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Player, team, match and tournament statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;
    private final PlayerMatchStatMapper playerMatchStatMapper;
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
        statisticsService.registerMatchStat(playerMatchStatMapper.toDomain(request));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // ---------- Player ----------

    @Operation(summary = "Get a player's win rate",
            description = "Percentage of matches won by the player out of matches played.")
    @GetMapping("/players/{playerId}/average-win-rate")
    public ResponseEntity<PlayerAverageResponse> getAverageWinRate(
            @PathVariable String playerId,
            @Parameter(description = "Optional tournament filter; omit for historical data across all tournaments")
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getAverageWinRate(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's average goals per match")
    @GetMapping("/players/{playerId}/average-goals")
    public ResponseEntity<PlayerAverageResponse> getAverageGoals(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getAverageGoals(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's average fouls committed per match")
    @GetMapping("/players/{playerId}/average-fouls")
    public ResponseEntity<PlayerAverageResponse> getAverageFouls(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getAverageFouls(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's average minutes played per match")
    @GetMapping("/players/{playerId}/average-minutes-played")
    public ResponseEntity<PlayerAverageResponse> getAverageMinutesPlayed(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getAverageMinutesPlayed(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's total number of matches played")
    @GetMapping("/players/{playerId}/matches-played")
    public ResponseEntity<MatchesPlayedResponse> getMatchesPlayed(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getMatchesPlayed(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's total goals scored (sum, not average)")
    @GetMapping("/players/{playerId}/total-goals")
    public ResponseEntity<TotalResponse> getPlayerTotalGoals(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getPlayerTotalGoals(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's total fouls committed (sum, not average)")
    @GetMapping("/players/{playerId}/total-fouls")
    public ResponseEntity<TotalResponse> getPlayerTotalFouls(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getPlayerTotalFouls(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's total assists")
    @GetMapping("/players/{playerId}/assists")
    public ResponseEntity<TotalResponse> getPlayerTotalAssists(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getPlayerTotalAssists(playerId, tournamentId)));
    }

    @Operation(summary = "Get a player's accumulated yellow and red cards")
    @GetMapping("/players/{playerId}/cards")
    public ResponseEntity<PlayerCardsResponse> getPlayerCards(
            @PathVariable String playerId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getPlayerCards(playerId, tournamentId)));
    }

    // ---------- Team ----------

    @Operation(
            summary = "Get a team's full statistics in the currently active tournament",
            description = "Resolves the active tournament by calling the Tournaments service. "
                    + "Unlike the other endpoints, this one does NOT accept a tournamentId "
                    + "parameter.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "502", description = "The Tournaments service is "
                    + "unavailable or reported no active tournament")
    })
    @GetMapping("/teams/{teamId}/statistics")
    public ResponseEntity<TeamStatisticsResponse> getTeamStatisticsInActiveTournament(
            @PathVariable String teamId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getTeamStatisticsInActiveTournament(teamId)));
    }

    @Operation(summary = "Get a team's match record (wins/draws/losses with percentages)")
    @GetMapping("/teams/{teamId}/match-record")
    public ResponseEntity<TeamMatchRecordResponse> getTeamMatchRecord(
            @PathVariable String teamId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getTeamMatchRecord(teamId, tournamentId)));
    }

    @Operation(summary = "Get a team's average goals scored per match")
    @GetMapping("/teams/{teamId}/average-goals")
    public ResponseEntity<TeamAverageResponse> getTeamAverageGoals(
            @PathVariable String teamId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getTeamAverageGoals(teamId, tournamentId)));
    }

    @Operation(summary = "Get a team's average fouls committed per match")
    @GetMapping("/teams/{teamId}/average-fouls")
    public ResponseEntity<TeamAverageResponse> getTeamAverageFouls(
            @PathVariable String teamId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getTeamAverageFouls(teamId, tournamentId)));
    }

    @Operation(summary = "Get a team's total fouls committed (sum, not average)")
    @GetMapping("/teams/{teamId}/total-fouls")
    public ResponseEntity<TotalResponse> getTeamTotalFouls(
            @PathVariable String teamId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getTeamTotalFouls(teamId, tournamentId)));
    }

    @Operation(summary = "Get a team's goals for, against, and goal difference")
    @GetMapping("/teams/{teamId}/goals")
    public ResponseEntity<TeamGoalsResponse> getTeamGoals(
            @PathVariable String teamId,
            @RequestParam(required = false) String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getTeamGoals(teamId, tournamentId)));
    }

    // ---------- Tournament ----------

    @Operation(summary = "Get the full tournament standings table",
            description = "Teams sorted by points, then goal difference, then goals for.")
    @GetMapping("/tournaments/{tournamentId}/standings")
    public ResponseEntity<TournamentStandingsResponse> getTournamentStandings(
            @PathVariable String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getTournamentStandings(tournamentId)));
    }

    @Operation(
            summary = "Get the public player ranking",
            description = "type=GOALS ranks players by most goals (top scorer). "
                    + "type=WINS ranks players by most matches won. "
                    + "type=FOULS ranks players by FEWEST fouls first (fair play table). "
                    + "type=MINUTES ranks players by most minutes accumulated.")
    @GetMapping("/rankings")
    public ResponseEntity<RankingResponse> getRanking(
            @Parameter(description = "GOALS, WINS, FOULS or MINUTES") @RequestParam RankingType type,
            @RequestParam(required = false) String tournamentId,
            @Parameter(description = "Top N results to return") @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getRanking(type, tournamentId, limit)));
    }

    @Operation(
            summary = "Get the goalkeeper ranking (fewest goals conceded)",
            description = "Only counts matches where the player was flagged as goalkeeper "
                    + "(\"goalkeeper\": true) in the match event.")
    @GetMapping("/goalkeeper-ranking")
    public ResponseEntity<GoalkeeperRankingResponse> getGoalkeeperRanking(
            @RequestParam(required = false) String tournamentId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getGoalkeeperRanking(tournamentId, limit)));
    }

    @Operation(
            summary = "Get average goals, fouls and cards per match across the whole tournament")
    @GetMapping("/tournaments/{tournamentId}/match-averages")
    public ResponseEntity<TournamentMatchAveragesResponse> getTournamentMatchAverages(
            @PathVariable String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getTournamentMatchAverages(tournamentId)));
    }

    @Operation(summary = "Get total yellow and red cards for the whole tournament")
    @GetMapping("/tournaments/{tournamentId}/cards")
    public ResponseEntity<CardsTotalResponse> getTournamentCardsTotal(
            @PathVariable String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getTournamentCardsTotal(tournamentId)));
    }

    @Operation(
            summary = "Generate and persist the tournament recognitions",
            description = "Computes the top scorer(s) and best defense team(s) and SAVES the "
                    + "result. Intended to be called by the Tournaments service when a "
                    + "tournament is finalized. If two or more players/teams are tied, ALL of "
                    + "them are included in the result. Calling this again for the same "
                    + "tournament REPLACES the previously saved recognition.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Recognition generated and saved")
    })
    @PostMapping("/tournaments/{tournamentId}/recognitions")
    public ResponseEntity<TournamentRecognitionResponse> generateTournamentRecognitions(
            @PathVariable String tournamentId) {
        TournamentRecognitionResponse response =
                responseMapper.toResponse(statisticsService.generateTournamentRecognitions(tournamentId));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Get the previously saved tournament recognitions",
            description = "Reads the saved recognition; does NOT recompute it. Use the POST "
                    + "endpoint first to generate it.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "No recognition has been generated "
                    + "yet for this tournament")
    })
    @GetMapping("/tournaments/{tournamentId}/recognitions")
    public ResponseEntity<TournamentRecognitionResponse> getTournamentRecognitions(
            @PathVariable String tournamentId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getTournamentRecognitions(tournamentId)));
    }

    // ---------- Match ----------

    @Operation(summary = "Get total yellow and red cards for a single match")
    @GetMapping("/matches/{matchId}/cards")
    public ResponseEntity<CardsTotalResponse> getMatchCardsTotal(@PathVariable String matchId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getMatchCardsTotal(matchId)));
    }

    @Operation(
            summary = "Get the result of a match for each team",
            description = "Returns WON/DRAWN/LOST per team for the given match. A walkover is "
                    + "represented simply as WON for the present team and LOST for the absent "
                    + "team.")
    @GetMapping("/matches/{matchId}/result")
    public ResponseEntity<MatchResultResponse> getMatchResult(@PathVariable String matchId) {
        return ResponseEntity.ok(responseMapper.toResponse(statisticsService.getMatchResult(matchId)));
    }
}

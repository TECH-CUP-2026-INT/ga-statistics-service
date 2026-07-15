package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.controller.swagger;

import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.request.MatchStatEventRequest;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.response.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Statistics", description = "Estadísticas de jugadores, equipos, partidos y torneos")
public interface StatisticsSwagger {

    @Operation(summary = "Registrar un evento de partido",
            description = "Endpoint interno consumido por el servicio de Competencia")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Evento registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error de validación en los datos de entrada"),
        @ApiResponse(responseCode = "409", description = "El jugador ya tiene estadísticas en este partido")
    })
    @PostMapping("/events")
    ResponseEntity<Void> registerMatchStat(@Valid @RequestBody MatchStatEventRequest request);

    @Operation(summary = "Porcentaje de victorias de un jugador")
    @GetMapping("/players/{playerId}/average-win-rate")
    ResponseEntity<PlayerAverageResponse> getAverageWinRate(
            @PathVariable String playerId,
            @Parameter(description = "Filtro opcional por torneo") @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Promedio de goles de un jugador")
    @GetMapping("/players/{playerId}/average-goals")
    ResponseEntity<PlayerAverageResponse> getAverageGoals(@PathVariable String playerId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Promedio de faltas de un jugador")
    @GetMapping("/players/{playerId}/average-fouls")
    ResponseEntity<PlayerAverageResponse> getAverageFouls(@PathVariable String playerId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Promedio de minutos jugados de un jugador")
    @GetMapping("/players/{playerId}/average-minutes-played")
    ResponseEntity<PlayerAverageResponse> getAverageMinutesPlayed(@PathVariable String playerId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Partidos jugados por un jugador")
    @GetMapping("/players/{playerId}/matches-played")
    ResponseEntity<MatchesPlayedResponse> getMatchesPlayed(@PathVariable String playerId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Total de goles de un jugador")
    @GetMapping("/players/{playerId}/total-goals")
    ResponseEntity<TotalResponse> getPlayerTotalGoals(@PathVariable String playerId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Total de faltas de un jugador")
    @GetMapping("/players/{playerId}/total-fouls")
    ResponseEntity<TotalResponse> getPlayerTotalFouls(@PathVariable String playerId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Total de asistencias de un jugador")
    @GetMapping("/players/{playerId}/assists")
    ResponseEntity<TotalResponse> getPlayerTotalAssists(@PathVariable String playerId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Tarjetas amarillas y rojas de un jugador")
    @GetMapping("/players/{playerId}/cards")
    ResponseEntity<PlayerCardsResponse> getPlayerCards(@PathVariable String playerId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Estadísticas de un equipo en el torneo activo")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "502", description = "Servicio de Torneos no disponible")})
    @GetMapping("/teams/{teamId}/statistics")
    ResponseEntity<TeamStatisticsResponse> getTeamStatisticsInActiveTournament(@PathVariable String teamId);

    @Operation(summary = "Récord de partidos de un equipo")
    @GetMapping("/teams/{teamId}/match-record")
    ResponseEntity<TeamMatchRecordResponse> getTeamMatchRecord(@PathVariable String teamId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Promedio de goles de un equipo")
    @GetMapping("/teams/{teamId}/average-goals")
    ResponseEntity<TeamAverageResponse> getTeamAverageGoals(@PathVariable String teamId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Promedio de faltas de un equipo")
    @GetMapping("/teams/{teamId}/average-fouls")
    ResponseEntity<TeamAverageResponse> getTeamAverageFouls(@PathVariable String teamId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Total de faltas de un equipo")
    @GetMapping("/teams/{teamId}/total-fouls")
    ResponseEntity<TotalResponse> getTeamTotalFouls(@PathVariable String teamId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Goles a favor, en contra y diferencia de un equipo")
    @GetMapping("/teams/{teamId}/goals")
    ResponseEntity<TeamGoalsResponse> getTeamGoals(@PathVariable String teamId, @RequestParam(required = false) String tournamentId);

    @Operation(summary = "Tabla de posiciones del torneo")
    @GetMapping("/tournaments/{tournamentId}/standings")
    ResponseEntity<TournamentStandingsResponse> getTournamentStandings(@PathVariable String tournamentId);

    @Operation(summary = "Ranking de jugadores",
            description = "type=GOALS: más goles, type=WINS: más victorias, type=FOULS: menos faltas, type=MINUTES: más minutos")
    @GetMapping("/rankings")
    ResponseEntity<RankingResponse> getRanking(
            @Parameter(description = "GOALS, WINS, FOULS o MINUTES") @RequestParam String type,
            @RequestParam(required = false) String tournamentId,
            @RequestParam(defaultValue = "10") int limit);

    @Operation(summary = "Ranking de porteros (menos goles recibidos)")
    @GetMapping("/goalkeeper-ranking")
    ResponseEntity<GoalkeeperRankingResponse> getGoalkeeperRanking(@RequestParam(required = false) String tournamentId, @RequestParam(defaultValue = "10") int limit);

    @Operation(summary = "Promedios del torneo")
    @GetMapping("/tournaments/{tournamentId}/match-averages")
    ResponseEntity<TournamentMatchAveragesResponse> getTournamentMatchAverages(@PathVariable String tournamentId);

    @Operation(summary = "Total de tarjetas del torneo")
    @GetMapping("/tournaments/{tournamentId}/cards")
    ResponseEntity<CardsTotalResponse> getTournamentCardsTotal(@PathVariable String tournamentId);

    @Operation(summary = "Generar reconocimientos del torneo")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Reconocimiento generado")})
    @PostMapping("/tournaments/{tournamentId}/recognitions")
    ResponseEntity<TournamentRecognitionResponse> generateTournamentRecognitions(@PathVariable String tournamentId);

    @Operation(summary = "Obtener reconocimientos del torneo")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "No se ha generado")})
    @GetMapping("/tournaments/{tournamentId}/recognitions")
    ResponseEntity<TournamentRecognitionResponse> getTournamentRecognitions(@PathVariable String tournamentId);

    @Operation(summary = "Total de tarjetas de un partido")
    @GetMapping("/matches/{matchId}/cards")
    ResponseEntity<CardsTotalResponse> getMatchCardsTotal(@PathVariable String matchId);

    @Operation(summary = "Resultado de un partido")
    @GetMapping("/matches/{matchId}/result")
    ResponseEntity<MatchResultResponse> getMatchResult(@PathVariable String matchId);
}

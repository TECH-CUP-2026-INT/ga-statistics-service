package co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.controller.swagger;
import java.util.UUID;
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

@Tag(name = "Statistics", description = "EstadÃ­sticas de jugadores, equipos, partidos y torneos")
public interface StatisticsSwagger {

    @Operation(summary = "Registrar un evento de partido",
            description = "Endpoint interno consumido por el servicio de Competencia")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Evento registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error de validaciÃ³n en los datos de entrada"),
        @ApiResponse(responseCode = "409", description = "El jugador ya tiene estadÃ­sticas en este partido")
    })
    @PostMapping("/events")
    ResponseEntity<Void> registerMatchStat(@Valid @RequestBody MatchStatEventRequest request);

    @Operation(summary = "Porcentaje de victorias de un jugador")
    @GetMapping("/players/{playerId}/average-win-rate")
    ResponseEntity<PlayerAverageResponse> getAverageWinRate(
            @PathVariable UUID playerId,
            @Parameter(description = "Filtro opcional por torneo") @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Promedio de goles de un jugador")
    @GetMapping("/players/{playerId}/average-goals")
    ResponseEntity<PlayerAverageResponse> getAverageGoals(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Promedio de faltas de un jugador")
    @GetMapping("/players/{playerId}/average-fouls")
    ResponseEntity<PlayerAverageResponse> getAverageFouls(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Promedio de minutos jugados de un jugador")
    @GetMapping("/players/{playerId}/average-minutes-played")
    ResponseEntity<PlayerAverageResponse> getAverageMinutesPlayed(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Partidos jugados por un jugador")
    @GetMapping("/players/{playerId}/matches-played")
    ResponseEntity<MatchesPlayedResponse> getMatchesPlayed(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Total de goles de un jugador")
    @GetMapping("/players/{playerId}/total-goals")
    ResponseEntity<TotalResponse> getPlayerTotalGoals(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Total de faltas de un jugador")
    @GetMapping("/players/{playerId}/total-fouls")
    ResponseEntity<TotalResponse> getPlayerTotalFouls(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Total de asistencias de un jugador")
    @GetMapping("/players/{playerId}/assists")
    ResponseEntity<TotalResponse> getPlayerTotalAssists(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Tarjetas amarillas y rojas de un jugador")
    @GetMapping("/players/{playerId}/cards")
    ResponseEntity<PlayerCardsResponse> getPlayerCards(@PathVariable UUID playerId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "EstadÃ­sticas de un equipo en un torneo",
            description = "Si no se envÃ­a tournamentId, resuelve el torneo activo llamando al servicio de Torneos.")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "502", description = "Servicio de Torneos no disponible")})
    @GetMapping("/teams/{teamId}/statistics")
    ResponseEntity<TeamStatisticsResponse> getTeamStatistics(
            @PathVariable UUID teamId,
            @Parameter(description = "ID del torneo (opcional, si no se envÃ­a busca el activo)") @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "RÃ©cord de partidos de un equipo")
    @GetMapping("/teams/{teamId}/match-record")
    ResponseEntity<TeamMatchRecordResponse> getTeamMatchRecord(@PathVariable UUID teamId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Promedio de goles de un equipo")
    @GetMapping("/teams/{teamId}/average-goals")
    ResponseEntity<TeamAverageResponse> getTeamAverageGoals(@PathVariable UUID teamId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Promedio de faltas de un equipo")
    @GetMapping("/teams/{teamId}/average-fouls")
    ResponseEntity<TeamAverageResponse> getTeamAverageFouls(@PathVariable UUID teamId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Total de faltas de un equipo")
    @GetMapping("/teams/{teamId}/total-fouls")
    ResponseEntity<TotalResponse> getTeamTotalFouls(@PathVariable UUID teamId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Goles a favor, en contra y diferencia de un equipo")
    @GetMapping("/teams/{teamId}/goals")
    ResponseEntity<TeamGoalsResponse> getTeamGoals(@PathVariable UUID teamId, @RequestParam(required = false) UUID tournamentId);

    @Operation(summary = "Tabla de posiciones del torneo")
    @GetMapping("/tournaments/{tournamentId}/standings")
    ResponseEntity<TournamentStandingsResponse> getTournamentStandings(@PathVariable UUID tournamentId);

    @Operation(summary = "Ranking de jugadores",
            description = "type=GOALS: mÃ¡s goles, type=WINS: mÃ¡s victorias, type=FOULS: menos faltas, type=MINUTES: mÃ¡s minutos")
    @GetMapping("/rankings")
    ResponseEntity<RankingResponse> getRanking(
            @Parameter(description = "GOALS, WINS, FOULS o MINUTES") @RequestParam String type,
            @RequestParam(required = false) UUID tournamentId,
            @RequestParam(defaultValue = "10") int limit);

    @Operation(summary = "Ranking de porteros (menos goles recibidos)")
    @GetMapping("/goalkeeper-ranking")
    ResponseEntity<GoalkeeperRankingResponse> getGoalkeeperRanking(@RequestParam(required = false) UUID tournamentId, @RequestParam(defaultValue = "10") int limit);

    @Operation(summary = "Promedios del torneo")
    @GetMapping("/tournaments/{tournamentId}/match-averages")
    ResponseEntity<TournamentMatchAveragesResponse> getTournamentMatchAverages(@PathVariable UUID tournamentId);

    @Operation(summary = "Total de tarjetas del torneo")
    @GetMapping("/tournaments/{tournamentId}/cards")
    ResponseEntity<CardsTotalResponse> getTournamentCardsTotal(@PathVariable UUID tournamentId);

    @Operation(summary = "Generar reconocimientos del torneo")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Reconocimiento generado")})
    @PostMapping("/tournaments/{tournamentId}/recognitions")
    ResponseEntity<TournamentRecognitionResponse> generateTournamentRecognitions(@PathVariable UUID tournamentId);

    @Operation(summary = "Obtener reconocimientos del torneo")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "No se ha generado")})
    @GetMapping("/tournaments/{tournamentId}/recognitions")
    ResponseEntity<TournamentRecognitionResponse> getTournamentRecognitions(@PathVariable UUID tournamentId);

    @Operation(summary = "Total de tarjetas de un partido")
    @GetMapping("/matches/{matchId}/cards")
    ResponseEntity<CardsTotalResponse> getMatchCardsTotal(@PathVariable UUID matchId);

    @Operation(summary = "Resultado de un partido")
    @GetMapping("/matches/{matchId}/result")
    ResponseEntity<MatchResultResponse> getMatchResult(@PathVariable UUID matchId);
}

package co.edu.escuelaing.techcup.statistics.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Objeto de DOMINIO: representa el desempeño de un jugador en un partido.
 * <p>
 * No tiene anotaciones de Spring Data, MongoDB ni de validación web.
 * Es la representación que usa la capa de negocio para calcular promedios,
 * totales y rankings.
 * <p>
 * La conversión hacia/desde el documento de MongoDB y desde el DTO de entrada
 * la realizan los mappers de MapStruct.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlayerMatchStatistic {

    private String id;
    private String playerId;
    private String teamId;
    private String matchId;
    private String tournamentId;

    private Integer goals;
    private Integer yellowCards;
    private Integer redCards;
    private Integer foulsCommitted;
    private Integer minutesPlayed;
    private Integer assists;
    private boolean goalkeeper;

    private MatchResult result;
    private LocalDateTime registeredAt;
}

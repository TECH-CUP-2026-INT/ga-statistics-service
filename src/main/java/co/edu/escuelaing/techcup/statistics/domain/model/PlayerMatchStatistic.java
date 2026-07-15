package co.edu.escuelaing.techcup.statistics.domain.model;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Objeto de DOMINIO: representa el desempeÃ±o de un jugador en un partido.
 * <p>
 * No tiene anotaciones de Spring Data, MongoDB ni de validaciÃ³n web.
 * Es la representaciÃ³n que usa la capa de negocio para calcular promedios,
 * totales y rankings.
 * <p>
 * La conversiÃ³n hacia/desde el documento de MongoDB y desde el DTO de entrada
 * la realizan los mappers de MapStruct.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class PlayerMatchStatistic {

    private String id;
    private UUID playerId;
    private UUID teamId;
    private UUID matchId;
    private UUID tournamentId;

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

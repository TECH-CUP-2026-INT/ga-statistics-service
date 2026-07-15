package co.edu.escuelaing.techcup.statistics.domain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Objeto de DOMINIO: el desempeno de un jugador en un partido.
 *
 * A proposito NO tiene ninguna anotacion de Spring Data / MongoDB ni de
 * validacion web. Es la representacion que usa la capa de negocio
 * (StatisticsServiceImpl) para calcular promedios, totales y rankings.
 *
 * La conversion hacia/desde el documento de MongoDB (PlayerMatchStat) y
 * desde el DTO de entrada (MatchStatEventRequest) la hace
 * PlayerMatchStatMapper (MapStruct) - el service nunca ve esas dos clases
 * directamente.
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

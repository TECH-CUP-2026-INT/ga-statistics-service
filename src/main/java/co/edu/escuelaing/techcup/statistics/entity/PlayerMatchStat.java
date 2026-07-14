package co.edu.escuelaing.techcup.statistics.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa el desempeño de UN jugador en UN partido específico.
 * Este es el registro atómico a partir del cual se calculan todos los
 * promedios y los rankings del servicio de estadísticas.
 *
 * Se recibe desde el servicio de Competencia (arbitraje en vivo) una vez
 * finaliza el partido.
 *
 * Coleccion de MongoDB (no tabla relacional): las agregaciones (promedios,
 * sumas) se calculan en la capa de servicio en vez de en la base de datos.
 */
@Document(collection = "player_match_stats")
@CompoundIndex(name = "uk_player_match", def = "{'playerId': 1, 'matchId': 1}", unique = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerMatchStat {

    @Id
    private String id;

    @Indexed
    private Long playerId;

    @Indexed
    private Long teamId;

    @Indexed
    private Long matchId;

    @Indexed
    private Long tournamentId;

    @Builder.Default
    private Integer goals = 0;

    @Builder.Default
    private Integer yellowCards = 0;

    @Builder.Default
    private Integer redCards = 0;

    @Builder.Default
    private Integer foulsCommitted = 0;

    @Builder.Default
    private Integer minutesPlayed = 0;

    @Builder.Default
    private Integer assists = 0;

    /** true si este jugador jugó como portero en este partido específico. */
    @Builder.Default
    private boolean goalkeeper = false;

    private MatchResult result;

    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();
}

package co.edu.escuelaing.techcup.statistics.entity;

import co.edu.escuelaing.techcup.statistics.domain.MatchResult;

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
 * NOTA: playerId, teamId, matchId y tournamentId son String (no Long) porque
 * los demás microservicios del sistema (Torneos, Equipos, Usuarios) usan
 * MongoDB con IDs tipo ObjectId (ej: "64f1a2b3c4d5e6f7a8b9c0d1"), no
 * identificadores numéricos.
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
    private String playerId;

    @Indexed
    private String teamId;

    @Indexed
    private String matchId;

    @Indexed
    private String tournamentId;

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

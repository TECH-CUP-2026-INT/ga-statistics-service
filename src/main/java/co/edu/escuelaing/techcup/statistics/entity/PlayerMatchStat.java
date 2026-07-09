package co.edu.escuelaing.techcup.statistics.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

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
 */
@Entity
@Table(
        name = "player_match_stats",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_player_match",
                columnNames = {"player_id", "match_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerMatchStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "player_id", nullable = false)
    private Long playerId;

    @Column(name = "team_id", nullable = false)
    private Long teamId;

    @Column(name = "match_id", nullable = false)
    private Long matchId;

    @Column(name = "tournament_id", nullable = false)
    private Long tournamentId;

    @Column(nullable = false)
    @Builder.Default
    private Integer goals = 0;

    @Column(name = "yellow_cards", nullable = false)
    @Builder.Default
    private Integer yellowCards = 0;

    @Column(name = "red_cards", nullable = false)
    @Builder.Default
    private Integer redCards = 0;

    @Column(name = "fouls_committed", nullable = false)
    @Builder.Default
    private Integer foulsCommitted = 0;

    @Column(name = "minutes_played", nullable = false)
    @Builder.Default
    private Integer minutesPlayed = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MatchResult result;

    @Column(name = "registered_at", nullable = false)
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();
}

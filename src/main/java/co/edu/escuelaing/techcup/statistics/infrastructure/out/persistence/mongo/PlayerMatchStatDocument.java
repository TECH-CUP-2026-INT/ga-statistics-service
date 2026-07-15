package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo;

import co.edu.escuelaing.techcup.statistics.domain.model.MatchResult;

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
 * Documento de MongoDB (capa de infraestructura). Es el desempeno de un
 * jugador en un partido, tal como se guarda en la coleccion
 * "player_match_stats". El resto de la aplicacion nunca ve esta clase --
 * solo el adaptador de persistencia (PlayerMatchStatRepositoryAdapter) y su
 * mapper.
 */
@Document(collection = "player_match_stats")
@CompoundIndex(name = "uk_player_match", def = "{'playerId': 1, 'matchId': 1}", unique = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerMatchStatDocument {

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

    @Builder.Default
    private boolean goalkeeper = false;

    private MatchResult result;

    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();
}

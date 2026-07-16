package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo;
import java.util.UUID;
import co.edu.escuelaing.techcup.statistics.domain.model.MatchResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "player_match_stats")
@CompoundIndex(name = "uk_player_match", def = "{'playerId': 1, 'matchId': 1}", unique = true)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlayerMatchStatDocument {

    @Id private String id;
    @Indexed private UUID playerId;
    @Indexed private UUID teamId;
    @Indexed private UUID matchId;
    @Indexed private UUID tournamentId;
    @Builder.Default private Integer goals = 0;
    @Builder.Default private Integer yellowCards = 0;
    @Builder.Default private Integer redCards = 0;
    @Builder.Default private Integer foulsCommitted = 0;
    @Builder.Default private Integer minutesPlayed = 0;
    @Builder.Default private Integer assists = 0;
    @Builder.Default private boolean goalkeeper = false;
    private MatchResult result;
    @CreatedDate private LocalDateTime registeredAt;
    @LastModifiedDate private LocalDateTime updatedAt;
    @CreatedBy private String createdBy;
    @LastModifiedBy private String lastModifiedBy;
}

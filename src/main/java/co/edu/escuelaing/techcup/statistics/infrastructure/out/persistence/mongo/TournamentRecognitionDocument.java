package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tournament_recognitions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TournamentRecognitionDocument {

    @Id private String id;
    @Indexed(unique = true) private UUID tournamentId;
    private List<UUID> topScorerPlayerIds;
    private long topScorersGoals;
    private List<UUID> bestDefenseTeamIds;
    private long bestDefenseGoalsAgainst;
    @CreatedDate private LocalDateTime generatedAt;
    @LastModifiedDate private LocalDateTime updatedAt;
    @CreatedBy private String createdBy;
    @LastModifiedBy private String lastModifiedBy;
}

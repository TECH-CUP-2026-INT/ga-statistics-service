package co.edu.escuelaing.techcup.statistics.infrastructure.out.persistence.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tournament_recognitions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TournamentRecognitionDocument {

    @Id private String id;
    @Indexed(unique = true) private String tournamentId;
    private List<String> topScorerPlayerIds;
    private long topScorersGoals;
    private List<String> bestDefenseTeamIds;
    private long bestDefenseGoalsAgainst;
    @Builder.Default private LocalDateTime generatedAt = LocalDateTime.now();
}

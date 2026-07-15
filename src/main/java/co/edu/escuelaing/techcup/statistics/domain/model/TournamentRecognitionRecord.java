package co.edu.escuelaing.techcup.statistics.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Objeto de DOMINIO del reconocimiento de un torneo (maximo goleador y
 * malla menos vencida). Sin anotaciones de persistencia; la conversion
 * hacia/desde el documento de MongoDB la hace TournamentRecognitionMapper.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TournamentRecognitionRecord {

    private String id;
    private String tournamentId;

    private List<String> topScorerPlayerIds;
    private long topScorersGoals;

    private List<String> bestDefenseTeamIds;
    private long bestDefenseGoalsAgainst;

    private LocalDateTime generatedAt;
}

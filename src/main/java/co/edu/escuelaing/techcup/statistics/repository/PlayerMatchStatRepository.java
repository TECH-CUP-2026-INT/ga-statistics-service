package co.edu.escuelaing.techcup.statistics.repository;

import co.edu.escuelaing.techcup.statistics.entity.PlayerMatchStat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerMatchStatRepository extends JpaRepository<PlayerMatchStat, Long> {

    boolean existsByPlayerIdAndMatchId(Long playerId, Long matchId);

    /**
     * Filtro reutilizable: si tournamentId es null, no filtra por torneo
     * (histórico general); si viene con valor, filtra a ese torneo.
     */

    @Query("""
            SELECT COUNT(p) FROM PlayerMatchStat p
            WHERE p.playerId = :playerId
            AND (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            """)
    long countMatchesPlayed(@Param("playerId") Long playerId,
                             @Param("tournamentId") Long tournamentId);

    @Query("""
            SELECT COUNT(p) FROM PlayerMatchStat p
            WHERE p.playerId = :playerId
            AND p.result = co.edu.escuelaing.techcup.statistics.entity.MatchResult.WON
            AND (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            """)
    long countMatchesWon(@Param("playerId") Long playerId,
                          @Param("tournamentId") Long tournamentId);

    @Query("""
            SELECT COALESCE(AVG(p.goals), 0) FROM PlayerMatchStat p
            WHERE p.playerId = :playerId
            AND (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            """)
    Double averageGoals(@Param("playerId") Long playerId,
                         @Param("tournamentId") Long tournamentId);

    @Query("""
            SELECT COALESCE(AVG(p.foulsCommitted), 0) FROM PlayerMatchStat p
            WHERE p.playerId = :playerId
            AND (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            """)
    Double averageFouls(@Param("playerId") Long playerId,
                         @Param("tournamentId") Long tournamentId);

    @Query("""
            SELECT COALESCE(AVG(p.minutesPlayed), 0) FROM PlayerMatchStat p
            WHERE p.playerId = :playerId
            AND (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            """)
    Double averageMinutesPlayed(@Param("playerId") Long playerId,
                                 @Param("tournamentId") Long tournamentId);

    Optional<PlayerMatchStat> findFirstByPlayerIdOrderByRegisteredAtDesc(Long playerId);

    // ---------- Rankings públicos ----------
    // Cada uno agrupa por jugador y ordena; se limita el tamaño con Pageable (Top N).

    @Query("""
            SELECT p.playerId AS playerId, SUM(p.goals) AS value
            FROM PlayerMatchStat p
            WHERE (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            GROUP BY p.playerId
            ORDER BY SUM(p.goals) DESC
            """)
    List<RankingRow> findGoalsRanking(@Param("tournamentId") Long tournamentId, Pageable pageable);

    @Query("""
            SELECT p.playerId AS playerId,
                   SUM(CASE WHEN p.result = co.edu.escuelaing.techcup.statistics.entity.MatchResult.WON THEN 1 ELSE 0 END) AS value
            FROM PlayerMatchStat p
            WHERE (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            GROUP BY p.playerId
            ORDER BY value DESC
            """)
    List<RankingRow> findWinsRanking(@Param("tournamentId") Long tournamentId, Pageable pageable);

    @Query("""
            SELECT p.playerId AS playerId, SUM(p.foulsCommitted) AS value
            FROM PlayerMatchStat p
            WHERE (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            GROUP BY p.playerId
            ORDER BY SUM(p.foulsCommitted) ASC
            """)
    List<RankingRow> findFairPlayRanking(@Param("tournamentId") Long tournamentId, Pageable pageable);

    @Query("""
            SELECT p.playerId AS playerId, SUM(p.minutesPlayed) AS value
            FROM PlayerMatchStat p
            WHERE (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            GROUP BY p.playerId
            ORDER BY SUM(p.minutesPlayed) DESC
            """)
    List<RankingRow> findMinutesRanking(@Param("tournamentId") Long tournamentId, Pageable pageable);

    /**
     * Proyección usada por las consultas de ranking (Spring Data la mapea
     * automáticamente a partir de los alias "playerId" y "value").
     */
    interface RankingRow {
        Long getPlayerId();
        Long getValue();
    }

    // ---------- Estadísticas de equipo ----------
    // Se derivan de PlayerMatchStat: cada partido que un equipo jugó aparece
    // repetido (una fila por jugador), por eso "partidos jugados" cuenta
    // matchId DISTINCT en vez de filas.

    @Query("""
            SELECT DISTINCT p.teamId FROM PlayerMatchStat p
            WHERE p.tournamentId = :tournamentId
            """)
    List<Long> findDistinctTeamIds(@Param("tournamentId") Long tournamentId);

    @Query("""
            SELECT COUNT(DISTINCT p.matchId) FROM PlayerMatchStat p
            WHERE p.teamId = :teamId
            AND (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            """)
    long countTeamMatchesPlayed(@Param("teamId") Long teamId,
                                 @Param("tournamentId") Long tournamentId);

    @Query("""
            SELECT COUNT(DISTINCT p.matchId) FROM PlayerMatchStat p
            WHERE p.teamId = :teamId
            AND p.result = :result
            AND (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            """)
    long countTeamMatchesByResult(@Param("teamId") Long teamId,
                                   @Param("tournamentId") Long tournamentId,
                                   @Param("result") co.edu.escuelaing.techcup.statistics.entity.MatchResult result);

    @Query("""
            SELECT COALESCE(SUM(p.goals), 0) FROM PlayerMatchStat p
            WHERE p.teamId = :teamId
            AND (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            """)
    long sumTeamGoalsFor(@Param("teamId") Long teamId,
                          @Param("tournamentId") Long tournamentId);

    /**
     * Goles en contra = suma de los goles anotados por los RIVALES en cada
     * partido que este equipo jugó (mismo matchId, teamId distinto).
     */
    @Query("""
            SELECT COALESCE(SUM(opp.goals), 0) FROM PlayerMatchStat opp
            WHERE opp.teamId <> :teamId
            AND opp.matchId IN (
                SELECT p.matchId FROM PlayerMatchStat p
                WHERE p.teamId = :teamId
                AND (:tournamentId IS NULL OR p.tournamentId = :tournamentId)
            )
            """)
    long sumTeamGoalsAgainst(@Param("teamId") Long teamId,
                              @Param("tournamentId") Long tournamentId);
}

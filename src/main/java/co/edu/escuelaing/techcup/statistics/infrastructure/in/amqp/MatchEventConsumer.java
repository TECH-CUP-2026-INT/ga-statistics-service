package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;

import co.edu.escuelaing.techcup.statistics.application.mapper.PlayerMatchStatMapper;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.in.StatisticsUseCase;
import co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.request.MatchStatEventRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de {@link MatchStatEvent}.
 * Recibe estadísticas de partido desde Competencia via RabbitMQ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchEventConsumer {

    private final StatisticsUseCase statisticsUseCase;
    private final PlayerMatchStatMapper playerMatchStatMapper;

    @RabbitListener(queues = "${rabbitmq.queue.match-events}")
    public void handleMatchStat(MatchStatEvent event) {
        log.info("MatchStatEvent recibido: playerId={}, matchId={}", event.playerId(), event.matchId());
        try {
            var request = new MatchStatEventRequest(
                    event.playerId(), event.teamId(), event.matchId(), event.tournamentId(),
                    event.result(), event.goals(), event.yellowCards(), event.redCards(),
                    event.foulsCommitted(), event.minutesPlayed(), event.assists(), event.goalkeeper());
            statisticsUseCase.registerMatchStat(playerMatchStatMapper.toDomain(request));
            log.info("Estadística registrada via RabbitMQ para playerId={}, matchId={}", event.playerId(), event.matchId());
        } catch (Exception e) {
            log.error("Error procesando MatchStatEvent: playerId={}, matchId={}: {}", event.playerId(), event.matchId(), e.getMessage());
        }
    }
}

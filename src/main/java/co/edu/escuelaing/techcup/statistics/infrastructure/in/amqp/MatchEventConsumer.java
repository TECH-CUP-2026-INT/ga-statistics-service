package co.edu.escuelaing.techcup.statistics.infrastructure.in.amqp;

import co.edu.escuelaing.techcup.statistics.application.mapper.PlayerMatchStatMapper;
import co.edu.escuelaing.techcup.statistics.domain.service.ports.in.StatisticsUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consumidor de eventos de partido desde RabbitMQ.
 * <p>
 * Escucha la cola {@code techcup.statistics.match-events} y procesa
 * los mensajes enviados por otros servicios (Competencia, Torneos).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MatchEventConsumer {

    private final StatisticsUseCase statisticsUseCase;
    private final PlayerMatchStatMapper playerMatchStatMapper;

    @RabbitListener(queues = "${rabbitmq.queue.match-events:techcup.statistics.match-events}")
    public void handleMatchEvent(MatchEventMessage message) {
        log.info("Evento de partido recibido desde RabbitMQ: playerId={}, matchId={}",
                message.playerId(), message.matchId());

        try {
            var statistic = playerMatchStatMapper.toDomain(
                    new co.edu.escuelaing.techcup.statistics.infrastructure.in.rest.dto.request.MatchStatEventRequest(
                            message.playerId(),
                            message.teamId(),
                            message.matchId(),
                            message.tournamentId(),
                            message.result(),
                            message.goals(),
                            message.yellowCards(),
                            message.redCards(),
                            message.foulsCommitted(),
                            message.minutesPlayed(),
                            message.assists(),
                            message.goalkeeper()
                    ));
            statisticsUseCase.registerMatchStat(statistic);
            log.info("Estadística registrada exitosamente desde RabbitMQ para playerId={}, matchId={}",
                    message.playerId(), message.matchId());
        } catch (Exception e) {
            log.error("Error procesando evento de RabbitMQ para playerId={}, matchId={}: {}",
                    message.playerId(), message.matchId(), e.getMessage());
        }
    }
}

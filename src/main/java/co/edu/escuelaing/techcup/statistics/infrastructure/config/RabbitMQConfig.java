package co.edu.escuelaing.techcup.statistics.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de RabbitMQ para el ecosistema TechCup.
 * <p>
 * Exchange compartido: <b>techcup.exchange</b> (topic).
 * Todas las colas deben bindearse a este exchange para que
 * cualquier servicio pueda publicar/consumir eventos.
 * <p>
 * Colas que define Estadísticas:
 * <ul>
 *   <li><b>techcup.statistics.match-events</b> — Eventos de partidos
 *       (goles, tarjetas, faltas, etc.) publicados por el servicio de
 *       Competencia/Partidos.</li>
 *   <li><b>techcup.statistics.tournament-events</b> — Eventos de ciclo
 *       de vida del torneo (finalización, activación, etc.) publicados
 *       por el servicio de Torneos.</li>
 * </ul>
 * <p>
 * Routing keys:
 * <ul>
 *   <li>{@code techcup.match.event.*} — Eventos de partido
 *       (ej: techcup.match.event.finished)</li>
 *   <li>{@code techcup.tournament.event.*} — Eventos de torneo
 *       (ej: techcup.tournament.event.finalized)</li>
 * </ul>
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.techcup:techcup.exchange}")
    private String techcupExchange;

    @Value("${rabbitmq.queue.match-events:techcup.statistics.match-events}")
    private String matchEventsQueue;

    @Value("${rabbitmq.routing-key.match-events:techcup.match.event.*}")
    private String matchEventsRoutingKey;

    @Value("${rabbitmq.queue.tournament-events:techcup.statistics.tournament-events}")
    private String tournamentEventsQueue;

    @Value("${rabbitmq.routing-key.tournament-events:techcup.tournament.event.*}")
    private String tournamentEventsRoutingKey;

    /** Exchange compartido para todos los eventos del sistema. */
    @Bean
    public TopicExchange techcupExchange() {
        return new TopicExchange(techcupExchange);
    }

    /** Cola para eventos de partido (goles, tarjetas, faltas, etc.). */
    @Bean
    public Queue matchEventsQueue() {
        return new Queue(matchEventsQueue, true);
    }

    @Bean
    public Binding matchEventsBinding(Queue matchEventsQueue, TopicExchange techcupExchange) {
        return BindingBuilder
                .bind(matchEventsQueue)
                .to(techcupExchange)
                .with(matchEventsRoutingKey);
    }

    /** Cola para eventos de ciclo de vida del torneo (finalización, etc.). */
    @Bean
    public Queue tournamentEventsQueue() {
        return new Queue(tournamentEventsQueue, true);
    }

    @Bean
    public Binding tournamentEventsBinding(Queue tournamentEventsQueue, TopicExchange techcupExchange) {
        return BindingBuilder
                .bind(tournamentEventsQueue)
                .to(techcupExchange)
                .with(tournamentEventsRoutingKey);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}

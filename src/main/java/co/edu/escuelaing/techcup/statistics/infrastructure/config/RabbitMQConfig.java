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
 * Configuración de RabbitMQ para el servicio de Estadísticas.
 * <p>
 * Define el exchange, las colas y los bindings necesarios para
 * recibir eventos de partidos desde otros servicios (Competencia, Torneos)
 * de manera asíncrona.
 * <p>
 * Pendiente de coordinar con el equipo de Notificaciones para
 * usar el mismo broker y definir los tópicos/payloads definitivos.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.statistics:techcup.statistics.exchange}")
    private String statisticsExchange;

    @Value("${rabbitmq.queue.match-events:techcup.statistics.match-events}")
    private String matchEventsQueue;

    @Value("${rabbitmq.routing-key.match-events:techcup.match.event.#}")
    private String matchEventsRoutingKey;

    @Bean
    public TopicExchange statisticsExchange() {
        return new TopicExchange(statisticsExchange);
    }

    @Bean
    public Queue matchEventsQueue() {
        return new Queue(matchEventsQueue, true);
    }

    @Bean
    public Binding matchEventsBinding(Queue matchEventsQueue, TopicExchange statisticsExchange) {
        return BindingBuilder
                .bind(matchEventsQueue)
                .to(statisticsExchange)
                .with(matchEventsRoutingKey);
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

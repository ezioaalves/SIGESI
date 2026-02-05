package com.sigesi.sigesi.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for event publishing.
 */
@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "sigesi_events";
    public static final String QUEUE_NAME = "sigesi_notifications";
    public static final String ROUTING_KEY_ASSIGNED = "demand.assigned";
    public static final String ROUTING_KEY_STATUS_CHANGED = "demand.status_changed";

    /**
     * Topic exchange for routing events.
     *
     * @return TopicExchange bean
     */
    @Bean
    public TopicExchange sigesiEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    /**
     * Queue for notifications.
     *
     * @return Queue bean
     */
    @Bean
    public Queue notificationsQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    /**
     * Binding for demand.assigned events.
     *
     * @return Binding bean
     */
    @Bean
    public Binding bindingDemandAssigned() {
        return BindingBuilder
            .bind(notificationsQueue())
            .to(sigesiEventsExchange())
            .with(ROUTING_KEY_ASSIGNED);
    }

    /**
     * Binding for demand.status_changed events.
     *
     * @return Binding bean
     */
    @Bean
    public Binding bindingDemandStatusChanged() {
        return BindingBuilder
            .bind(notificationsQueue())
            .to(sigesiEventsExchange())
            .with(ROUTING_KEY_STATUS_CHANGED);
    }

    /**
     * RabbitTemplate with JSON message converter.
     *
     * @param connectionFactory Connection factory
     * @return RabbitTemplate bean
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    /**
     * Jackson JSON message converter for RabbitMQ.
     *
     * @return Jackson2JsonMessageConverter bean
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

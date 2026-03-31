package com.smartcourier.delivery.config;

import com.smartcourier.delivery.messaging.DeliveryEventPublisher;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    public static final String QUEUE = "tracking.delivery.status.queue";

    @Bean
    public TopicExchange eventExchange() {
        return new TopicExchange(DeliveryEventPublisher.EXCHANGE);
    }

    @Bean
    public Queue trackingQueue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding statusBinding(Queue trackingQueue, TopicExchange eventExchange) {
        return BindingBuilder.bind(trackingQueue)
                .to(eventExchange)
                .with(DeliveryEventPublisher.ROUTING_KEY);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

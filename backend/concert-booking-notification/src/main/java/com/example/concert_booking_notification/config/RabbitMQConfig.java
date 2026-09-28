package com.example.concert_booking_notification.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "concert.booking.exchange";
    public static final String QUEUE_COMMANDE_PAYEE = "notification.commande.payee.queue";
    public static final String ROUTING_KEY_COMMANDE_PAYEE = "commande.payee";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue queueCommandePayee() {
        
        return new Queue(QUEUE_COMMANDE_PAYEE, true);
    }

    @Bean
    public Binding bindingCommandePayee(Queue queueCommandePayee, TopicExchange exchange) {
        return BindingBuilder
                .bind(queueCommandePayee)
                .to(exchange)
                .with(ROUTING_KEY_COMMANDE_PAYEE);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
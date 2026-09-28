package com.example.concert_booking_api.metier.commande;

import com.example.concert_booking_api.core.config.RabbitMqConfig;
import com.example.concert_booking_api.metier.commande.dto.CommandePayeeEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommandeEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public CommandeEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publierCommandePayee(CommandePayeeEvent event) {
        rabbitTemplate.convertAndSend(
                RabbitMqConfig.EXCHANGE,
                RabbitMqConfig.ROUTING_KEY_COMMANDE_PAYEE,
                event
        );
    }
}
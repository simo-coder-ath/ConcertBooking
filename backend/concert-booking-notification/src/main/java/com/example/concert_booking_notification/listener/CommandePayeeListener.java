package com.example.concert_booking_notification.listener;

import com.example.concert_booking_notification.config.RabbitMQConfig;
import com.example.concert_booking_notification.dto.CommandePayeeEvent;
import com.example.concert_booking_notification.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class CommandePayeeListener {

    private static final Logger log =
            LoggerFactory.getLogger(CommandePayeeListener.class);

    private final EmailService emailService;

    public CommandePayeeListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_COMMANDE_PAYEE)
    public void onCommandePayee(CommandePayeeEvent event) {

        log.info("Événement reçu : commande payée [{}]", event.reference());

        try {
            emailService.envoyerConfirmationCommande(event);
        } catch (Exception exception) {
          
            log.error(
                    "Échec de l'envoi de l'email pour la commande {}",
                    event.reference(),
                    exception
            );
        }
    }
}
package com.example.concert_booking_notification.service;

import com.example.concert_booking_notification.dto.CommandePayeeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void envoyerConfirmationCommande(CommandePayeeEvent event) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(event.emailClient());
        message.setSubject("Confirmation de votre commande " + event.reference());
        message.setText(
                "Bonjour " + event.prenomClient() + ",\n\n"
                        + "Votre commande " + event.reference() + " d'un montant de "
                        + event.montantTotal() + " € a bien été payée.\n\n"
                        + (event.pdfTicketUrl() != null
                                ? "Votre billet est disponible ici : " + event.pdfTicketUrl() + "\n\n"
                                : "")
                        + "Merci et à bientôt !"
        );

        mailSender.send(message);

        log.info("Email de confirmation envoyé à {}", event.emailClient());
    }
}
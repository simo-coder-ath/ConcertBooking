package com.example.concert_booking_api.metier.commande.dto;

import java.math.BigDecimal;

public record CommandePayeeEvent(
        Long commandeId,
        String reference,
        String emailClient,
        String prenomClient,
        BigDecimal montantTotal,
        String pdfTicketUrl
) {
}
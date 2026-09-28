package com.example.concert_booking_notification.dto;

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
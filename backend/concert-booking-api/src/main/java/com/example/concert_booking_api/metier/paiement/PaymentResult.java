package com.example.concert_booking_api.metier.paiement;

import java.math.BigDecimal;

public record PaymentResult(
        boolean succes,
        String transactionId,
        BigDecimal montant,
        String provider,
        String message
) {
}
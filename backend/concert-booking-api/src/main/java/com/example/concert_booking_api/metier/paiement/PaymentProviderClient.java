package com.example.concert_booking_api.metier.paiement;

import java.math.BigDecimal;

public interface PaymentProviderClient {

    PaymentResult confirmerPaiement(
            String paymentToken,
            BigDecimal montantAttendu
    );
}
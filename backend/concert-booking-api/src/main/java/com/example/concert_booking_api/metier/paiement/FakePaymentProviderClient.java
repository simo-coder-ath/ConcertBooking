package com.example.concert_booking_api.metier.paiement;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;





@Component
public class FakePaymentProviderClient implements PaymentProviderClient {

    @Override
    public PaymentResult confirmerPaiement(
            String paymentToken,
            BigDecimal montantAttendu
    ) {

        if (paymentToken == null || paymentToken.trim().isEmpty()) {
            return new PaymentResult(
                    false,
                    null,
                    montantAttendu,
                    "FAKE_PROVIDER",
                    "Token de paiement absent."
            );
        }

        String transactionId =
                "TX-" + UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 16)
                        .toUpperCase();

        return new PaymentResult(
                true,
                transactionId,
                montantAttendu,
                "FAKE_PROVIDER",
                "Paiement accepté."
        );
    }
}
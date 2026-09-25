package com.example.concert_booking_api.app.paiement.dto;

import com.example.concert_booking_api.dao.entity.Facture;
import com.example.concert_booking_api.dao.enums.StatutCommande;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PaiementResponse(

        Long factureId,
        Long commandeId,
        String transactionId,
        BigDecimal montantRegle,
        String paymentProvider,
        String statutPaiement,
        String pdfTicketUrl,
        StatutCommande statutCommande,
        OffsetDateTime createdAt

) {

    public static PaiementResponse from(Facture facture) {

        return new PaiementResponse(
                facture.getId(),
                facture.getCommande().getId(),
                facture.getTransactionId(),
                facture.getMontantRegle(),
                facture.getPaymentProvider(),
                facture.getStatutPaiement(),
                facture.getPdfTicketUrl(),
                facture.getCommande().getStatut(),
                facture.getCreatedAt()
        );
    }
}
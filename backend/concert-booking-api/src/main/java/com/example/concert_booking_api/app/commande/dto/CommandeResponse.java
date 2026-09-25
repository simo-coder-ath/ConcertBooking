package com.example.concert_booking_api.app.commande.dto;

import com.example.concert_booking_api.dao.entity.Commande;
import com.example.concert_booking_api.dao.enums.StatutCommande;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CommandeResponse(
        Long id,
        String reference,
        Long utilisateurId,
        StatutCommande statut,
        BigDecimal montantTotal,
        OffsetDateTime expiresAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    public static CommandeResponse from(Commande commande) {

        return new CommandeResponse(
                commande.getId(),
                commande.getReference(),
                commande.getUtilisateur().getId(),
                commande.getStatut(),
                commande.getMontantTotal(),
                commande.getExpiresAt(),
                commande.getCreatedAt(),
                commande.getUpdatedAt()
        );
    }
}
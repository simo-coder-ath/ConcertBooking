package com.example.concert_booking_api.app.reservation.dto;

import com.example.concert_booking_api.dao.entity.Commande;
import com.example.concert_booking_api.dao.enums.StatutCommande;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record ReservationResponse(

        Long commandeId,
        String reference,
        Long utilisateurId,
        Long evenementId,
        List<Long> placeIds,
        StatutCommande statut,
        BigDecimal montantTotal,
        OffsetDateTime expiresAt,
        OffsetDateTime createdAt

) {

    public static ReservationResponse from(Commande commande) {

        List<Long> placeIds = commande.getLignes()
                .stream()
                .map(ligne -> ligne.getPlace().getId())
                .toList();

        Long evenementId = null;

        if (!commande.getLignes().isEmpty()) {
            evenementId = commande.getLignes()
                    .get(0)
                    .getPlace()
                    .getEvenement()
                    .getId();
        }

        return new ReservationResponse(
                commande.getId(),
                commande.getReference(),
                commande.getUtilisateur().getId(),
                evenementId,
                placeIds,
                commande.getStatut(),
                commande.getMontantTotal(),
                commande.getExpiresAt(),
                commande.getCreatedAt()
        );
    }
}
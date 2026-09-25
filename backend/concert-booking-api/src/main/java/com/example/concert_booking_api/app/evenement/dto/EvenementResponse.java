package com.example.concert_booking_api.app.evenement.dto;

import com.example.concert_booking_api.dao.entity.Evenement;
import com.example.concert_booking_api.dao.enums.StatutEvenement;

import java.time.OffsetDateTime;








public record EvenementResponse(

        Long id,
        Long salleId,
        String titre,
        String description,
        String imageBannerUrl,
        OffsetDateTime dateOuvertureVentes,
        OffsetDateTime dateEvenement,
        StatutEvenement statut,
        OffsetDateTime createdAt

) {



    public static EvenementResponse from(Evenement evenement) {

        return new EvenementResponse(
                evenement.getId(),
                evenement.getSalle().getId(),
                evenement.getTitre(),
                evenement.getDescription(),
                evenement.getImageBannerUrl(),
                evenement.getDateOuvertureVentes(),
                evenement.getDateEvenement(),
                evenement.getStatut(),
                evenement.getCreatedAt()

                
        );
    }
}
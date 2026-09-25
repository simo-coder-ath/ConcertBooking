package com.example.concert_booking_api.app.evenement.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;




public record ModifierEvenementRequest(






        @NotNull(message = "L'identifiant de la salle est obligatoire.")
        Long salleId,



        @NotBlank(message = "Le titre de l'événement est obligatoire.")
        String titre,


        String description,

        String imageBannerUrl,






        @Future(message = "La date d'ouverture des ventes doit être dans le futur.")
        OffsetDateTime dateOuvertureVentes,




        @NotNull(message = "La date de l'événement est obligatoire.")



        @Future(message = "La date de l'événement doit être dans le futur.")
        OffsetDateTime dateEvenement


        

) {}
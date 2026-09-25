package com.example.concert_booking_api.app.reservation.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReserverPlacesRequest(

        @NotNull(message = "L'identifiant de l'utilisateur est obligatoire.")
        Long utilisateurId,

        @NotNull(message = "L'identifiant de l'événement est obligatoire.")
        Long evenementId,

        @NotEmpty(message = "Vous devez sélectionner au moins une place.")
        List<Long> placeIds

) {}
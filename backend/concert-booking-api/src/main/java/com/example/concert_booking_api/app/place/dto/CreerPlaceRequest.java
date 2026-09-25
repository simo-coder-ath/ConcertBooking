package com.example.concert_booking_api.app.place.dto;

import jakarta.validation.constraints.NotNull;

public record CreerPlaceRequest(

        @NotNull(message = "L'identifiant de l'événement est obligatoire.")
        Long evenementId,

        @NotNull(message = "L'identifiant du siège est obligatoire.")
        Long siegeId,

        @NotNull(message = "L'identifiant de la catégorie de prix est obligatoire.")
        Long categoriePrixId

) {}
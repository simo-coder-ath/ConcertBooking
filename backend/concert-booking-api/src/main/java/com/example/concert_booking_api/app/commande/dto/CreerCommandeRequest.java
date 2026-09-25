package com.example.concert_booking_api.app.commande.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreerCommandeRequest(

        @NotNull(message = "L'identifiant utilisateur est obligatoire.")
        Long utilisateurId,

        @NotEmpty(message = "La commande doit contenir au moins une place.")
        List<Long> placeIds

) {
}
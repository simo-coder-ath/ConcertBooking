package com.example.concert_booking_api.app.salle.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreerSalleRequest(

        @NotBlank(message = "Le nom de la salle est obligatoire.")
        String nom,

        @NotBlank(message = "L'adresse de la salle est obligatoire.")
        String adresse,

        @NotBlank(message = "La ville est obligatoire.")
        String ville,

        @NotBlank(message = "Le code postal est obligatoire.")
        String codePostal,

        @NotNull(message = "La capacité est obligatoire.")
        @Min(value = 1, message = "La capacité doit être supérieure à 0.")
        Integer capacite

) {}
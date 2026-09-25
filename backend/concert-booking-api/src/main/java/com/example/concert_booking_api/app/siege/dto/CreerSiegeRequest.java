package com.example.concert_booking_api.app.siege.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreerSiegeRequest(

        @NotNull(message = "L'identifiant de la salle est obligatoire.")
        Long salleId,

        @NotBlank(message = "Le rang est obligatoire.")
        String rang,

        @NotBlank(message = "Le numéro du siège est obligatoire.")
        String numero,

        String zone

) {}
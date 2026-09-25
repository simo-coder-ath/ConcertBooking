package com.example.concert_booking_api.app.utilisateur.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ModifierUtilisateurRequest(

        @NotBlank(message = "L'email est obligatoire.")
        @Email(message = "L'email est invalide.")
        String email,

        @NotBlank(message = "Le nom est obligatoire.")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire.")
        String prenom,

        String telephone

) {}
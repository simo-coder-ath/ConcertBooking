package com.example.concert_booking_api.app.utilisateur.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreerUtilisateurRequest(

        @NotBlank(message = "L'email est obligatoire.")
        @Email(message = "L'email est invalide.")
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire.")
        @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères.")
        String motDePasse,

        @NotBlank(message = "Le nom est obligatoire.")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire.")
        String prenom,

        String telephone

) {}
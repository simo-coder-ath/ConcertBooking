package com.example.concert_booking_api.app.paiement.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PayerCommandeRequest(

        @NotNull(message = "L'identifiant de la commande est obligatoire.")
        Long commandeId,

        @NotNull(message = "L'identifiant de l'utilisateur est obligatoire.")
        Long utilisateurId,

        @NotNull(message = "Le montant est obligatoire.")
        @DecimalMin(value = "0.00", message = "Le montant doit être positif ou nul.")
        BigDecimal montant,

        @NotBlank(message = "Le token de paiement est obligatoire.")
        String paymentToken

) {}
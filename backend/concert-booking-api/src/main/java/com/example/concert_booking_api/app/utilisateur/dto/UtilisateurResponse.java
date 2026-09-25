package com.example.concert_booking_api.app.utilisateur.dto;

import com.example.concert_booking_api.dao.entity.Utilisateur;
import com.example.concert_booking_api.dao.enums.RoleUtilisateur;

import java.time.OffsetDateTime;

public record UtilisateurResponse(

        Long id,
        String email,
        String nom,
        String prenom,
        String telephone,
        RoleUtilisateur role,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt

) {

    public static UtilisateurResponse from(Utilisateur utilisateur) {

        return new UtilisateurResponse(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getTelephone(),
                utilisateur.getRole(),
                utilisateur.getCreatedAt(),
                utilisateur.getUpdatedAt()
        );
    }
}
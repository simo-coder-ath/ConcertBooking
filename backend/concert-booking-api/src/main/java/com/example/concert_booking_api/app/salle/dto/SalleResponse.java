package com.example.concert_booking_api.app.salle.dto;

import com.example.concert_booking_api.dao.entity.Salle;

public record SalleResponse(

        Long id,
        String nom,
        String adresse,
        String ville,
        String codePostal,
        Integer capacite

) {

    public static SalleResponse from(Salle salle) {

        return new SalleResponse(
                salle.getId(),
                salle.getNom(),
                salle.getAdresse(),
                salle.getVille(),
                salle.getCodePostal(),
                salle.getCapacite()
        );
    }
}
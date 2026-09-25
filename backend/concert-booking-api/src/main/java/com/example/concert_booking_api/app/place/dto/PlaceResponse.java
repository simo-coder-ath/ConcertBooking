package com.example.concert_booking_api.app.place.dto;

import com.example.concert_booking_api.dao.entity.Place;
import com.example.concert_booking_api.dao.enums.StatutPlace;

import java.math.BigDecimal;

public record PlaceResponse(

        Long id,

        Long evenementId,

        Long siegeId,

        String rang,

        String numero,

        String zone,

        Long categoriePrixId,

        String categoriePrixNom,

        BigDecimal prix,

        StatutPlace statut

) {

    public static PlaceResponse from(Place place) {

        return new PlaceResponse(
                place.getId(),
                place.getEvenement().getId(),
                place.getSiege().getId(),
                place.getSiege().getRang(),
                place.getSiege().getNumero(),
                place.getSiege().getZone(),
                place.getCategoriePrix().getId(),
                place.getCategoriePrix().getNom(),
                place.getCategoriePrix().getPrix(),
                place.getStatut()
        );
    }
}
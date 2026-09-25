package com.example.concert_booking_api.app.siege.dto;

import com.example.concert_booking_api.dao.entity.Siege;

public record SiegeResponse(

        Long id,
        Long salleId,
        String rang,
        String numero,
        String zone

) {

    public static SiegeResponse from(Siege siege) {

        return new SiegeResponse(
                siege.getId(),
                siege.getSalle().getId(),
                siege.getRang(),
                siege.getNumero(),
                siege.getZone()
        );
    }
}
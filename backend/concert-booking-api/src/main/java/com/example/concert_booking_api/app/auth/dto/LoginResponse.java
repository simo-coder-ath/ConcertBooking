package com.example.concert_booking_api.app.auth.dto;

public record LoginResponse(
        String token,
        String tokenType,
        Long utilisateurId
) {

    public static LoginResponse of(String token, Long utilisateurId) {
        return new LoginResponse(token, "Bearer", utilisateurId);
    }
}
package com.example.concert_booking_api.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expirationMs;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-ms}") long expirationMs
    ) {

        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException(
                    "La clé secrète JWT est obligatoire."
            );
        }

        if (secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException(
                    "La clé secrète JWT doit contenir au moins 32 octets."
            );
        }

        if (expirationMs <= 0) {
            throw new IllegalArgumentException(
                    "La durée d'expiration JWT doit être supérieure à 0."
            );
        }

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expirationMs = expirationMs;
    }

    public String generateToken(String email) {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "L'email est obligatoire pour générer un JWT."
            );
        }

        Date maintenant = new Date();
        Date expiration = new Date(
                maintenant.getTime() + expirationMs
        );

        return Jwts.builder()
                .subject(email.trim().toLowerCase())
                .issuedAt(maintenant)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    public String extractEmail(String token) {

        Claims claims = extractAllClaims(token);

        return claims.getSubject();
    }

    public boolean isTokenValid(
            String token,
            String email
    ) {

        if (token == null || token.isBlank()) {
            return false;
        }

        if (email == null || email.isBlank()) {
            return false;
        }

        try {
            String emailDuToken = extractEmail(token);

            return email.trim()
                    .equalsIgnoreCase(emailDuToken)
                    && !isTokenExpired(token);

        } catch (Exception exception) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {

        try {
            Date expiration =
                    extractAllClaims(token).getExpiration();

            return expiration.before(new Date());

        } catch (Exception exception) {
            return true;
        }
    }

    private Claims extractAllClaims(String token) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Le token JWT est obligatoire."
            );
        }

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
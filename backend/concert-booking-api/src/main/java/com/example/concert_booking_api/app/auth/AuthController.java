package com.example.concert_booking_api.app.auth;

import com.example.concert_booking_api.app.auth.dto.LoginRequest;
import com.example.concert_booking_api.app.auth.dto.LoginResponse;
import com.example.concert_booking_api.core.security.JwtService;
import com.example.concert_booking_api.dao.entity.Utilisateur;
import com.example.concert_booking_api.dao.repository.UtilisateurRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UtilisateurRepository utilisateurRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UtilisateurRepository utilisateurRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.motDePasse()
                )
        );

        String email = request.email().trim().toLowerCase();

        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(email)
                .orElseThrow();

        String token = jwtService.generateToken(email);

        return ResponseEntity.ok(
                LoginResponse.of(token, utilisateur.getId())
        );
    }
}
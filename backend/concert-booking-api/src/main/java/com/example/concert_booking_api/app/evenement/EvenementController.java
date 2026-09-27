package com.example.concert_booking_api.app.evenement;

import com.example.concert_booking_api.app.evenement.dto.CreerEvenementRequest;
import com.example.concert_booking_api.app.evenement.dto.EvenementResponse;
import com.example.concert_booking_api.app.evenement.dto.ModifierEvenementRequest;
import com.example.concert_booking_api.dao.entity.Evenement;
import com.example.concert_booking_api.dao.enums.RoleUtilisateur;
import com.example.concert_booking_api.metier.evenement.EvenementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    private final EvenementService evenementService;

    public EvenementController(EvenementService evenementService) {
        this.evenementService = evenementService;
    }

    @PostMapping
    public ResponseEntity<EvenementResponse> creerEvenement(
            @Valid @RequestBody CreerEvenementRequest request,
            Authentication authentication
    ) {
        Evenement evenement = evenementService.creerEvenement(
                request.titre(),
                request.description(),
                request.imageBannerUrl(),
                request.salleId(),
                request.dateOuvertureVentes(),
                request.dateEvenement(),
                extraireRole(authentication)
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EvenementResponse.from(evenement));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EvenementResponse> modifierEvenement(
            @PathVariable Long id,
            @Valid @RequestBody ModifierEvenementRequest request,
            Authentication authentication
    ) {
        Evenement evenement = evenementService.modifierEvenement(
                id,
                request.salleId(),
                request.titre(),
                request.description(),
                request.imageBannerUrl(),
                request.dateOuvertureVentes(),
                request.dateEvenement(),
                extraireRole(authentication)
        );

        return ResponseEntity.ok(EvenementResponse.from(evenement));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvenementResponse> trouverEvenement(@PathVariable Long id) {
        Evenement evenement = evenementService.trouverEvenement(id);
        return ResponseEntity.ok(EvenementResponse.from(evenement));
    }

    @GetMapping
    public ResponseEntity<List<EvenementResponse>> listerEvenements() {
        List<EvenementResponse> evenements = evenementService
                .listerEvenements()
                .stream()
                .map(EvenementResponse::from)
                .toList();

        return ResponseEntity.ok(evenements);
    }

    @PostMapping("/{id}/publier")
    public ResponseEntity<EvenementResponse> publierEvenement(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Evenement evenement = evenementService.publierEvenement(
                id,
                extraireRole(authentication)
        );

        return ResponseEntity.ok(EvenementResponse.from(evenement));
    }

    @PostMapping("/{id}/annuler")
    public ResponseEntity<EvenementResponse> annulerEvenement(
            @PathVariable Long id,
            Authentication authentication
    ) {
        Evenement evenement = evenementService.annulerEvenement(
                id,
                extraireRole(authentication)
        );

        return ResponseEntity.ok(EvenementResponse.from(evenement));
    }

    private RoleUtilisateur extraireRole(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "Authentification requise."
            );
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring(5))
                .map(RoleUtilisateur::valueOf)
                .findFirst()
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException(
                        "Rôle utilisateur introuvable."
                ));
    }
}
package com.example.concert_booking_api.app.utilisateur;

import com.example.concert_booking_api.app.utilisateur.dto.CreerUtilisateurRequest;
import com.example.concert_booking_api.app.utilisateur.dto.ModifierUtilisateurRequest;
import com.example.concert_booking_api.app.utilisateur.dto.UtilisateurResponse;
import com.example.concert_booking_api.dao.entity.Utilisateur;
import com.example.concert_booking_api.metier.utilisateur.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping
    public ResponseEntity<UtilisateurResponse> creerUtilisateur(
            @Valid @RequestBody CreerUtilisateurRequest request
    ) {

        Utilisateur utilisateur = utilisateurService.creerUtilisateur(
                request.email(),
                request.motDePasse(),
                request.nom(),
                request.prenom(),
                request.telephone()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(UtilisateurResponse.from(utilisateur));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> modifierUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody ModifierUtilisateurRequest request
    ) {

        Utilisateur utilisateur = utilisateurService.modifierUtilisateur(
                id,
                request.email(),
                request.nom(),
                request.prenom(),
                request.telephone()
        );

        return ResponseEntity.ok(
                UtilisateurResponse.from(utilisateur)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurResponse> trouverUtilisateur(
            @PathVariable Long id
    ) {

        Utilisateur utilisateur = utilisateurService.trouverUtilisateur(id);

        return ResponseEntity.ok(
                UtilisateurResponse.from(utilisateur)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerUtilisateur(
            @PathVariable Long id
    ) {

        utilisateurService.supprimerUtilisateur(id);

        return ResponseEntity.noContent().build();
    }
}
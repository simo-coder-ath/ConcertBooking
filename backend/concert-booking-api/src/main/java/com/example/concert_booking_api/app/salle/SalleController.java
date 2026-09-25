package com.example.concert_booking_api.app.salle;

import com.example.concert_booking_api.app.salle.dto.CreerSalleRequest;
import com.example.concert_booking_api.app.salle.dto.ModifierSalleRequest;
import com.example.concert_booking_api.app.salle.dto.SalleResponse;
import com.example.concert_booking_api.dao.entity.Salle;
import com.example.concert_booking_api.metier.salle.SalleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salles")
public class SalleController {

    private final SalleService salleService;

    public SalleController(SalleService salleService) {
        this.salleService = salleService;
    }



    @PostMapping
    public ResponseEntity<SalleResponse> creerSalle(
            @Valid @RequestBody CreerSalleRequest request
    ) {

        Salle salle = salleService.creerSalle(
                request.nom(),
                request.adresse(),
                request.ville(),
                request.codePostal(),
                request.capacite()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SalleResponse.from(salle));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SalleResponse> modifierSalle(
            @PathVariable Long id,
            @Valid @RequestBody ModifierSalleRequest request
    ) {

        Salle salle = salleService.modifierSalle(
                id,
                request.nom(),
                request.adresse(),
                request.ville(),
                request.codePostal(),
                request.capacite()
        );

        return ResponseEntity.ok(
                SalleResponse.from(salle)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalleResponse> trouverSalle(
            @PathVariable Long id
    ) {

        Salle salle = salleService.trouverSalle(id);

        return ResponseEntity.ok(
                SalleResponse.from(salle)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerSalle(
            @PathVariable Long id
    ) {

        salleService.supprimerSalle(id);

        return ResponseEntity.noContent().build();
    }
}
package com.example.concert_booking_api.app.siege;

import com.example.concert_booking_api.app.siege.dto.CreerSiegeRequest;
import com.example.concert_booking_api.app.siege.dto.ModifierSiegeRequest;
import com.example.concert_booking_api.app.siege.dto.SiegeResponse;
import com.example.concert_booking_api.dao.entity.Siege;
import com.example.concert_booking_api.metier.siege.SiegeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sieges")
public class SiegeController {

    private final SiegeService siegeService;

    public SiegeController(SiegeService siegeService) {
        this.siegeService = siegeService;
    }

    @PostMapping
    public ResponseEntity<SiegeResponse> creerSiege(
            @Valid @RequestBody CreerSiegeRequest request
    ) {

        Siege siege = siegeService.creerSiege(
                request.salleId(),
                request.rang(),
                request.numero(),
                request.zone()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SiegeResponse.from(siege));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SiegeResponse> modifierSiege(
            @PathVariable Long id,
            @Valid @RequestBody ModifierSiegeRequest request
    ) {

        Siege siege = siegeService.modifierSiege(
                id,
                request.salleId(),
                request.rang(),
                request.numero(),
                request.zone()
        );

        return ResponseEntity.ok(
                SiegeResponse.from(siege)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<SiegeResponse> trouverSiege(
            @PathVariable Long id
    ) {

        Siege siege = siegeService.trouverSiege(id);

        return ResponseEntity.ok(
                SiegeResponse.from(siege)
        );
    }

    @GetMapping("/salle/{salleId}")
    public ResponseEntity<List<SiegeResponse>> listerSiegesSalle(
            @PathVariable Long salleId
    ) {

        List<SiegeResponse> sieges = siegeService
                .listerSiegesParSalle(salleId)
                .stream()
                .map(SiegeResponse::from)
                .toList();

        return ResponseEntity.ok(sieges);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerSiege(
            @PathVariable Long id
    ) {

        siegeService.supprimerSiege(id);

        return ResponseEntity.noContent().build();
    }
}



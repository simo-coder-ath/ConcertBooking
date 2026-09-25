package com.example.concert_booking_api.app.evenement;

import com.example.concert_booking_api.app.evenement.dto.CreerEvenementRequest;
import com.example.concert_booking_api.app.evenement.dto.EvenementResponse;
import com.example.concert_booking_api.app.evenement.dto.ModifierEvenementRequest;
import com.example.concert_booking_api.dao.entity.Evenement;
import com.example.concert_booking_api.metier.evenement.EvenementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
            @Valid @RequestBody CreerEvenementRequest request
    ) {

        Evenement evenement = evenementService.creerEvenement(
                request.salleId(),
                request.titre(),
                request.description(),
                request.imageBannerUrl(),
                request.dateOuvertureVentes(),
                request.dateEvenement()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(EvenementResponse.from(evenement));
    }







    @PutMapping("/{id}")
    public ResponseEntity<EvenementResponse> modifierEvenement(
            @PathVariable Long id,
            @Valid @RequestBody ModifierEvenementRequest request
    ) {

        Evenement evenement = evenementService.modifierEvenement(
                id,
                request.salleId(),
                request.titre(),
                request.description(),
                request.imageBannerUrl(),
                request.dateOuvertureVentes(),
                request.dateEvenement()
        );

        return ResponseEntity.ok(
                EvenementResponse.from(evenement)
        );
    }








    @GetMapping("/{id}")
    public ResponseEntity<EvenementResponse> trouverEvenement(
            @PathVariable Long id
    ) {

        Evenement evenement = evenementService.trouverEvenement(id);

        return ResponseEntity.ok(
                EvenementResponse.from(evenement)
        );
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
            @PathVariable Long id
    ) {

        Evenement evenement = evenementService.publierEvenement(id);

        return ResponseEntity.ok(
                EvenementResponse.from(evenement)
        );
    }














    @PostMapping("/{id}/annuler")
    public ResponseEntity<EvenementResponse> annulerEvenement(
            @PathVariable Long id
    ) {

        Evenement evenement = evenementService.annulerEvenement(id);

        return ResponseEntity.ok(
                EvenementResponse.from(evenement)
        );
    }
}
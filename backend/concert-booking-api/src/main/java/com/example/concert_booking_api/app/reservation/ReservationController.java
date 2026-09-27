package com.example.concert_booking_api.app.reservation;

import com.example.concert_booking_api.app.reservation.dto.ReservationResponse;
import com.example.concert_booking_api.app.reservation.dto.ReserverPlacesRequest;
import com.example.concert_booking_api.dao.entity.Commande;
import com.example.concert_booking_api.dao.entity.LigneCommande;
import com.example.concert_booking_api.metier.reservation.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reserverPlaces(
            @Valid @RequestBody ReserverPlacesRequest request
    ) {

        Commande commande =
                reservationService.reserverPlaces(
                        request.utilisateurId(),
                        request.evenementId(),
                        request.placeIds()
                );

        List<LigneCommande> lignes =
                reservationService.listerLignesCommande(
                        commande.getId()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ReservationResponse.from(
                                commande,
                                lignes
                        )
                );
    }
}
package com.example.concert_booking_api.app.place;

import com.example.concert_booking_api.app.place.dto.CreerPlaceRequest;
import com.example.concert_booking_api.app.place.dto.PlaceResponse;
import com.example.concert_booking_api.dao.entity.Place;
import com.example.concert_booking_api.dao.enums.StatutPlace;
import com.example.concert_booking_api.metier.place.PlaceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping
    public ResponseEntity<PlaceResponse> creerPlace(
            @Valid @RequestBody CreerPlaceRequest request
    ) {

        Place place = placeService.creerPlace(
                request.evenementId(),
                request.siegeId(),
                request.categoriePrixId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PlaceResponse.from(place));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceResponse> trouverPlace(
            @PathVariable Long id
    ) {

        Place place = placeService.trouverPlace(id);

        return ResponseEntity.ok(
                PlaceResponse.from(place)
        );
    }

    @GetMapping("/evenement/{evenementId}")
    public ResponseEntity<List<PlaceResponse>> listerPlacesEvenement(
            @PathVariable Long evenementId
    ) {

        List<PlaceResponse> places = placeService
                .listerPlacesParEvenement(evenementId)
                .stream()
                .map(PlaceResponse::from)
                .toList();

        return ResponseEntity.ok(places);
    }

    @GetMapping("/evenement/{evenementId}/disponibles")
    public ResponseEntity<List<PlaceResponse>> listerPlacesDisponibles(
            @PathVariable Long evenementId
    ) {

        List<PlaceResponse> places = placeService
                .listerPlacesDisponibles(evenementId)
                .stream()
                .map(PlaceResponse::from)
                .toList();

        return ResponseEntity.ok(places);
    }

    @GetMapping("/evenement/{evenementId}/categorie/{categoriePrixId}")
    public ResponseEntity<List<PlaceResponse>> listerPlacesParCategorie(
            @PathVariable Long evenementId,
            @PathVariable Long categoriePrixId
    ) {

        List<PlaceResponse> places = placeService
                .listerPlacesParCategorie(evenementId, categoriePrixId)
                .stream()
                .map(PlaceResponse::from)
                .toList();

        return ResponseEntity.ok(places);
    }
}
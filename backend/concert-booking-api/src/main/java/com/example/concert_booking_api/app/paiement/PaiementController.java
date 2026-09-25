package com.example.concert_booking_api.app.paiement;

import com.example.concert_booking_api.app.paiement.dto.PaiementResponse;
import com.example.concert_booking_api.app.paiement.dto.PayerCommandeRequest;
import com.example.concert_booking_api.dao.entity.Facture;
import com.example.concert_booking_api.metier.paiement.PaiementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paiements")
public class PaiementController {

    private final PaiementService paiementService;

    public PaiementController(PaiementService paiementService) {
        this.paiementService = paiementService;
    }





    @PostMapping
    public ResponseEntity<PaiementResponse> payerCommande(
            @Valid @RequestBody PayerCommandeRequest request
    ) {

        Facture facture = paiementService.payerCommande(
                request.commandeId(),
                request.utilisateurId(),
                request.montant(),
                request.paymentToken()
        );

        return ResponseEntity.ok(
                PaiementResponse.from(facture)
        );
    }

    
}
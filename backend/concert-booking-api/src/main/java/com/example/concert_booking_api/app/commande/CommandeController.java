package com.example.concert_booking_api.app.commande;

import com.example.concert_booking_api.app.commande.dto.CommandeResponse;
import com.example.concert_booking_api.app.commande.dto.CreerCommandeRequest;
import com.example.concert_booking_api.dao.entity.Commande;
import com.example.concert_booking_api.metier.commande.CommandeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;





@RestController
@RequestMapping("/api/commandes")
public class CommandeController {

    private final CommandeService commandeService;

    public CommandeController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

   
    @PostMapping
    public ResponseEntity<CommandeResponse> creerCommande(
            @Valid @RequestBody CreerCommandeRequest request
    ) {

        Commande commande = commandeService.creerCommande(
                request.utilisateurId(),
                request.placeIds()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CommandeResponse.from(commande));
    }









    @GetMapping("/{id}")
    public ResponseEntity<CommandeResponse> trouverCommande(
            @PathVariable Long id
    ) {

        Commande commande = commandeService.trouverCommande(id);

        return ResponseEntity.ok(
                CommandeResponse.from(commande)
        );
    }
}
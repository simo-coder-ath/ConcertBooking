package com.example.concert_booking_api.metier.reservation;

import com.example.concert_booking_api.core.exception.BusinessException;
import com.example.concert_booking_api.core.exception.ResourceNotFoundException;
import com.example.concert_booking_api.dao.entity.Commande;
import com.example.concert_booking_api.dao.entity.Evenement;
import com.example.concert_booking_api.dao.entity.LigneCommande;
import com.example.concert_booking_api.dao.enums.StatutEvenement;
import com.example.concert_booking_api.dao.repository.EvenementRepository;
import com.example.concert_booking_api.dao.repository.LigneCommandeRepository;
import com.example.concert_booking_api.metier.commande.CommandeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class ReservationService {

    private final EvenementRepository evenementRepository;
    private final CommandeService commandeService;
    private final LigneCommandeRepository ligneCommandeRepository;

    public ReservationService(
            EvenementRepository evenementRepository,
            CommandeService commandeService,
            LigneCommandeRepository ligneCommandeRepository
    ) {
        this.evenementRepository = evenementRepository;
        this.commandeService = commandeService;
        this.ligneCommandeRepository = ligneCommandeRepository;
    }

    public Commande reserverPlaces(
            Long utilisateurId,
            Long evenementId,
            List<Long> placeIds
    ) {

        Evenement evenement =
                trouverEvenement(evenementId);

        verifierEvenementReservable(evenement);

        verifierPlacesNonVides(placeIds);

        return commandeService.creerCommande(
                utilisateurId,
                placeIds
        );
    }

    @Transactional(readOnly = true)
    public List<LigneCommande> listerLignesCommande(
            Long commandeId
    ) {

        if (commandeId == null) {
            throw new BusinessException(
                    "L'identifiant de la commande est obligatoire."
            );
        }

        return ligneCommandeRepository.findByCommandeId(
                commandeId
        );
    }

    private Evenement trouverEvenement(
            Long evenementId
    ) {

        if (evenementId == null) {

            throw new BusinessException(
                    "L'identifiant de l'événement "
                            + "est obligatoire."
            );
        }

        return evenementRepository.findById(
                evenementId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Événement introuvable."
                )
        );
    }

    private void verifierEvenementReservable(
            Evenement evenement
    ) {

        if (evenement.getStatut()
                != StatutEvenement.PUBLIE) {

            throw new BusinessException(
                    "Les réservations ne sont pas ouvertes "
                            + "pour cet événement."
            );
        }

        OffsetDateTime maintenant =
                OffsetDateTime.now();

        if (evenement.getDateOuvertureVentes() != null
                && maintenant.isBefore(
                        evenement.getDateOuvertureVentes()
                )) {

            throw new BusinessException(
                    "Les ventes ne sont pas encore ouvertes."
            );
        }

        if (evenement.getDateEvenement() != null
                && !evenement.getDateEvenement()
                        .isAfter(maintenant)) {

            throw new BusinessException(
                    "Cet événement est déjà passé."
            );
        }
    }

    private void verifierPlacesNonVides(
            List<Long> placeIds
    ) {

        if (placeIds == null
                || placeIds.isEmpty()) {

            throw new BusinessException(
                    "Vous devez sélectionner "
                            + "au moins une place."
            );
        }
    }
}
package com.example.concert_booking_api.metier.commande;

import com.example.concert_booking_api.core.exception.BusinessException;
import com.example.concert_booking_api.core.exception.ResourceNotFoundException;
import com.example.concert_booking_api.dao.entity.CategoriePrix;
import com.example.concert_booking_api.dao.entity.Commande;
import com.example.concert_booking_api.dao.entity.LigneCommande;
import com.example.concert_booking_api.dao.entity.Place;
import com.example.concert_booking_api.dao.entity.Utilisateur;
import com.example.concert_booking_api.dao.enums.StatutCommande;
import com.example.concert_booking_api.dao.enums.StatutPlace;
import com.example.concert_booking_api.dao.repository.CommandeRepository;
import com.example.concert_booking_api.dao.repository.LigneCommandeRepository;
import com.example.concert_booking_api.dao.repository.PlaceRepository;
import com.example.concert_booking_api.dao.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class CommandeService {

    private static final long DUREE_RESERVATION_MINUTES = 10;

    private final CommandeRepository commandeRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PlaceRepository placeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;

    public CommandeService(
            CommandeRepository commandeRepository,
            UtilisateurRepository utilisateurRepository,
            PlaceRepository placeRepository,
            LigneCommandeRepository ligneCommandeRepository
    ) {
        this.commandeRepository = commandeRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.placeRepository = placeRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
    }

   





    public Commande creerCommande(
            Long utilisateurId,
            List<Long> placeIds
    ) {

        Utilisateur utilisateur =
                trouverUtilisateur(utilisateurId);

        verifierListePlaces(placeIds);

        verifierPasDeDoublons(placeIds);

        List<Place> places = new ArrayList<>();

        for (Long placeId : placeIds) {

            Place place =
                    placeRepository.findByIdForUpdate(placeId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Place introuvable : " + placeId
                                    )
                            );

            if (place.getStatut() != StatutPlace.DISPONIBLE) {

                throw new BusinessException(
                        "La place "
                                + placeId
                                + " n'est plus disponible."
                );
            }

            places.add(place);
        }

        verifierMemeEvenement(places);

        BigDecimal montantTotal =
                calculerMontantTotal(places);

    


        for (Place place : places) {

            place.setStatut(
                    StatutPlace.VERROUILLEE
            );

            placeRepository.save(place);
        }

      

        Commande commande = new Commande();

        commande.setReference(
                genererReference()
        );

        commande.setUtilisateur(
                utilisateur
        );

        commande.setStatut(
                StatutCommande.EN_ATTENTE_DE_PAIEMENT
        );

        commande.setMontantTotal(
                montantTotal
        );

        commande.setExpiresAt(
                OffsetDateTime.now()
                        .plusMinutes(
                                DUREE_RESERVATION_MINUTES
                        )
        );

        commande =
                commandeRepository.save(commande);

      


        for (Place place : places) {

            CategoriePrix categoriePrix =
                    place.getCategoriePrix();

            LigneCommande ligne =
                    new LigneCommande();

            ligne.setCommande(
                    commande
            );

            ligne.setPlace(
                    place
            );

            ligne.setPrixUnitaire(
                    categoriePrix.getPrix()
            );

            ligneCommandeRepository.save(
                    ligne
            );
        }

        return commande;
    }

   


    public Commande paiementEchoue(
            Long commandeId
    ) {

        Commande commande =
                trouverCommande(commandeId);

        verifierCommandeEnAttente(
                commande
        );

        libererPlacesCommande(
                commandeId
        );

        commande.setStatut(
                StatutCommande.ECHOUEE
        );

        return commandeRepository.save(
                commande
        );
    }




    public Commande annulerCommandeTimeout(
            Long commandeId
    ) {

        Commande commande =
                trouverCommande(commandeId);

        verifierCommandeEnAttente(
                commande
        );

        if (commande.getExpiresAt()
                .isAfter(OffsetDateTime.now())) {

            throw new BusinessException(
                    "La commande n'est pas encore expirée."
            );
        }

        libererPlacesCommande(
                commandeId
        );

        commande.setStatut(
                StatutCommande.ANNULEE_TIMEOUT
        );

        return commandeRepository.save(
                commande
        );
    }

  
    public int traiterCommandesExpirees() {

        List<Commande> commandesExpirees =
                commandeRepository
                        .findByStatutAndExpiresAtBefore(
                                StatutCommande.EN_ATTENTE_DE_PAIEMENT,
                                OffsetDateTime.now()
                        );

        int nombreTraitees = 0;

        for (Commande commande : commandesExpirees) {

            libererPlacesCommande(
                    commande.getId()
            );

            commande.setStatut(
                    StatutCommande.ANNULEE_TIMEOUT
            );

            commandeRepository.save(
                    commande
            );

            nombreTraitees++;
        }

        return nombreTraitees;
    }

   
    private void libererPlacesCommande(
            Long commandeId
    ) {

        List<LigneCommande> lignes =
                ligneCommandeRepository
                        .findByCommandeId(
                                commandeId
                        );

        for (LigneCommande ligne : lignes) {

            Place place =
                    placeRepository
                            .findByIdForUpdate(
                                    ligne.getPlace().getId()
                            )
                            .orElse(null);

            if (place == null) {
                continue;
            }

            if (place.getStatut()
                    == StatutPlace.VERROUILLEE) {

                place.setStatut(
                        StatutPlace.DISPONIBLE
                );

                placeRepository.save(
                        place
                );
            }
        }
    }

 
    private BigDecimal calculerMontantTotal(
            List<Place> places
    ) {

        BigDecimal total =
                BigDecimal.ZERO;

        for (Place place : places) {

            CategoriePrix categoriePrix =
                    place.getCategoriePrix();

            if (categoriePrix == null
                    || categoriePrix.getPrix() == null) {

                throw new BusinessException(
                        "La place "
                                + place.getId()
                                + " possède une catégorie "
                                + "de prix invalide."
                );
            }

            total = total.add(
                    categoriePrix.getPrix()
            );
        }

        return total;
    }

  
    private void verifierMemeEvenement(
            List<Place> places
    ) {

        Long evenementId =
                places.get(0)
                        .getEvenement()
                        .getId();

        for (Place place : places) {

            if (!place.getEvenement()
                    .getId()
                    .equals(evenementId)) {

                throw new BusinessException(
                        "Toutes les places d'une commande "
                                + "doivent appartenir au même événement."
                );
            }
        }
    }

   
    private void verifierListePlaces(
            List<Long> placeIds
    ) {

        if (placeIds == null
                || placeIds.isEmpty()) {

            throw new BusinessException(
                    "La commande doit contenir au moins une place."
            );
        }
    }

  
    private void verifierPasDeDoublons(
            List<Long> placeIds
    ) {

        Set<Long> placesUniques =
                new HashSet<>(placeIds);

        if (placesUniques.size()
                != placeIds.size()) {

            throw new BusinessException(
                    "Une même place ne peut pas être "
                            + "présente plusieurs fois dans une commande."
            );
        }
    }

   
    private void verifierCommandeEnAttente(
            Commande commande
    ) {

        if (commande.getStatut()
                != StatutCommande.EN_ATTENTE_DE_PAIEMENT) {

            throw new BusinessException(
                    "Cette commande n'est plus "
                            + "en attente de paiement."
            );
        }
    }

   
    private Utilisateur trouverUtilisateur(
            Long utilisateurId
    ) {

        if (utilisateurId == null) {

            throw new BusinessException(
                    "L'identifiant utilisateur est obligatoire."
            );
        }

        return utilisateurRepository
                .findById(utilisateurId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Utilisateur introuvable."
                        )
                );
    }

   


    @Transactional(readOnly = true)
    public Commande trouverCommande(
            Long commandeId
    ) {

        if (commandeId == null) {

            throw new BusinessException(
                    "L'identifiant de la commande est obligatoire."
            );
        }

        return commandeRepository
                .findById(commandeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Commande introuvable."
                        )
                );
    }





    private String genererReference() {

        return "CMD-"
                + UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 12)
                        .toUpperCase();
    }
}
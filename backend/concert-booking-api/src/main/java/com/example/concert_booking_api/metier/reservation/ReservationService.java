package com.example.concert_booking_api.metier.reservation;

import com.example.concert_booking_api.dao.entity.Evenement;
import com.example.concert_booking_api.dao.entity.Commande;
import com.example.concert_booking_api.dao.enums.StatutEvenement;
import com.example.concert_booking_api.dao.repository.EvenementRepository;
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




    public ReservationService(
            EvenementRepository evenementRepository,
            CommandeService commandeService
    ) {


        this.evenementRepository = evenementRepository;
        this.commandeService = commandeService;
    }






    public Commande reserverPlaces(
            Long utilisateurId,
            Long evenementId,
            List<Long> placeIds
    ) {






        Evenement evenement = trouverEvenement(evenementId);

        verifierEvenementReservable(evenement);

        verifierPlacesNonVides(placeIds);

    
        Commande commande =
                commandeService.creerCommande(
                        utilisateurId,
                        placeIds
                );



      
        if (commande.getLignes() != null) {





        }

        return commande;
    }









    private Evenement trouverEvenement(Long evenementId) {

        if (evenementId == null) {
            throw new IllegalArgumentException(
                    "L'identifiant de l'événement est obligatoire."
            );
        }

        return evenementRepository.findById(evenementId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Événement introuvable."
                        )
                );
    }











    private void verifierEvenementReservable(
            Evenement evenement
    ) {

       
        if (evenement.getStatut() != StatutEvenement.PUBLIE) {
            throw new IllegalStateException(
                    "Les réservations ne sont pas ouvertes pour cet événement."
            );
        }



        OffsetDateTime maintenant =
                OffsetDateTime.now();

       
        if (evenement.getDateOuvertureVentes() != null
                && maintenant.isBefore(
                evenement.getDateOuvertureVentes()
        )) {

            throw new IllegalStateException(
                    "Les ventes ne sont pas encore ouvertes."
            );
        }







        if (evenement.getDateEvenement() != null
                && !evenement.getDateEvenement().isAfter(maintenant)) {

            throw new IllegalStateException(
                    "Cet événement est déjà passé."
            );
        }
    }







    private void verifierPlacesNonVides(
            List<Long> placeIds
    ) {




        if (placeIds == null || placeIds.isEmpty()) {


            throw new IllegalArgumentException(
                    "Vous devez sélectionner au moins une place."
            );





            
        }
    }
}
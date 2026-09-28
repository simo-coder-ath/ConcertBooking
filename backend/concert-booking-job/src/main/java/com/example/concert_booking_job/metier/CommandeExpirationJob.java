package com.example.concert_booking_job.metier;

import com.example.concert_booking_job.dao.entity.Commande;
import com.example.concert_booking_job.dao.entity.LigneCommande;
import com.example.concert_booking_job.dao.entity.Place;
import com.example.concert_booking_job.dao.entity.StatutCommande;
import com.example.concert_booking_job.dao.entity.StatutPlace;
import com.example.concert_booking_job.dao.repository.CommandeRepository;
import com.example.concert_booking_job.dao.repository.LigneCommandeRepository;
import com.example.concert_booking_job.dao.repository.PlaceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CommandeExpirationJob {

    private static final Logger log =
            LoggerFactory.getLogger(CommandeExpirationJob.class);

    private final CommandeRepository commandeRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final PlaceRepository placeRepository;

    public CommandeExpirationJob(
            CommandeRepository commandeRepository,
            LigneCommandeRepository ligneCommandeRepository,
            PlaceRepository placeRepository
    ) {
        this.commandeRepository = commandeRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.placeRepository = placeRepository;
    }

    // Toutes les 60 secondes — ajustez selon la fenêtre de paiement réelle
    @Scheduled(fixedRate = 60 * 1000)
    @Transactional
    public void libererCommandesExpirees() {

        List<Commande> commandesExpirees =
                commandeRepository.findByStatutAndExpiresAtBefore(
                        StatutCommande.EN_ATTENTE_DE_PAIEMENT,
                        OffsetDateTime.now()
                );

        if (commandesExpirees.isEmpty()) {
            log.debug("Aucune commande expirée à traiter.");
            return;
        }

        for (Commande commande : commandesExpirees) {
            libererCommande(commande);
        }

        log.info("{} commande(s) expirée(s) traitée(s).", commandesExpirees.size());
    }

    private void libererCommande(Commande commande) {

        commande.setStatut(StatutCommande.ANNULEE_TIMEOUT);
        commande.setUpdatedAt(OffsetDateTime.now());
        commandeRepository.save(commande);

        List<LigneCommande> lignes =
                ligneCommandeRepository.findByCommandeId(commande.getId());

        for (LigneCommande ligne : lignes) {
            Place place = ligne.getPlace();

            // On ne libère que si la place n'a pas déjà été vendue entre-temps
            if (place.getStatut() == StatutPlace.VERROUILLEE
                    || place.getStatut() == StatutPlace.RESERVEE) {

                place.setStatut(StatutPlace.DISPONIBLE);
                placeRepository.save(place); // @Version protège contre les accès concurrents
            }
        }

        log.info(
                "Commande {} expirée : {} place(s) libérée(s).",
                commande.getReference(),
                lignes.size()
        );
    }
}
package com.example.concert_booking_api.metier.salle;

import com.example.concert_booking_api.core.exception.BusinessException;
import com.example.concert_booking_api.core.exception.ResourceNotFoundException;
import com.example.concert_booking_api.dao.entity.Evenement;
import com.example.concert_booking_api.dao.entity.Salle;
import com.example.concert_booking_api.dao.enums.StatutEvenement;
import com.example.concert_booking_api.dao.repository.EvenementRepository;
import com.example.concert_booking_api.dao.repository.SalleRepository;
import com.example.concert_booking_api.dao.repository.SiegeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SalleService {

    private final SalleRepository salleRepository;
    private final SiegeRepository siegeRepository;
    private final EvenementRepository evenementRepository;

    public SalleService(
            SalleRepository salleRepository,
            SiegeRepository siegeRepository,
            EvenementRepository evenementRepository
    ) {
        this.salleRepository = salleRepository;
        this.siegeRepository = siegeRepository;
        this.evenementRepository = evenementRepository;
    }

    public Salle creerSalle(
            String nom,
            String adresse,
            String ville,
            String codePostal,
            Integer capacite
    ) {

        verifierInformationsSalle(
                nom,
                adresse,
                ville,
                codePostal,
                capacite
        );

        Salle salle = new Salle();

        salle.setNom(nom.trim());
        salle.setAdresse(adresse.trim());
        salle.setVille(ville.trim());
        salle.setCodePostal(codePostal.trim());
        salle.setCapacite(capacite);

        return salleRepository.save(salle);
    }

    public Salle modifierSalle(
            Long salleId,
            String nom,
            String adresse,
            String ville,
            String codePostal,
            Integer capacite
    ) {

        Salle salle = trouverSalle(salleId);

        verifierInformationsSalle(
                nom,
                adresse,
                ville,
                codePostal,
                capacite
        );

        long nombreSieges =
                siegeRepository
                        .findBySalleId(salleId)
                        .size();

        if (capacite < nombreSieges) {

            throw new BusinessException(
                    "La capacité ne peut pas être inférieure "
                            + "au nombre de sièges configurés."
            );
        }

        salle.setNom(nom.trim());
        salle.setAdresse(adresse.trim());
        salle.setVille(ville.trim());
        salle.setCodePostal(codePostal.trim());
        salle.setCapacite(capacite);

        return salleRepository.save(salle);
    }

    public void supprimerSalle(
            Long salleId
    ) {

        Salle salle = trouverSalle(salleId);

        List<Evenement> evenements =
                evenementRepository.findBySalleId(
                        salleId
                );

        for (Evenement evenement : evenements) {

            StatutEvenement statut =
                    evenement.getStatut();

            if (statut == StatutEvenement.PUBLIE
                    || statut == StatutEvenement.BROUILLON) {

                throw new BusinessException(
                        "Impossible de supprimer cette salle : "
                                + "elle est utilisée par un événement."
                );
            }
        }

        salleRepository.delete(salle);
    }

    @Transactional(readOnly = true)
    public Salle trouverSalle(
            Long salleId
    ) {

        if (salleId == null) {

            throw new BusinessException(
                    "L'identifiant de la salle "
                            + "est obligatoire."
            );
        }

        return salleRepository.findById(
                salleId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Salle introuvable."
                )
        );
    }

    @Transactional(readOnly = true)
    public List<Salle> listerSalles() {
        return salleRepository.findAll();
    }

    private void verifierInformationsSalle(
            String nom,
            String adresse,
            String ville,
            String codePostal,
            Integer capacite
    ) {

        if (nom == null
                || nom.trim().isEmpty()) {

            throw new BusinessException(
                    "Le nom de la salle "
                            + "est obligatoire."
            );
        }

        if (adresse == null
                || adresse.trim().isEmpty()) {

            throw new BusinessException(
                    "L'adresse de la salle "
                            + "est obligatoire."
            );
        }

        if (ville == null
                || ville.trim().isEmpty()) {

            throw new BusinessException(
                    "La ville est obligatoire."
            );
        }

        if (codePostal == null
                || codePostal.trim().isEmpty()) {

            throw new BusinessException(
                    "Le code postal "
                            + "est obligatoire."
            );
        }

        if (capacite == null
                || capacite <= 0) {

            throw new BusinessException(
                    "La capacité de la salle doit être "
                            + "strictement positive."
            );
        }
    }
}
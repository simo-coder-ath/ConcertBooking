package com.example.concert_booking_api.metier.evenement;

import com.example.concert_booking_api.core.exception.BusinessException;
import com.example.concert_booking_api.core.exception.ResourceNotFoundException;
import com.example.concert_booking_api.dao.entity.CategoriePrix;
import com.example.concert_booking_api.dao.entity.Evenement;
import com.example.concert_booking_api.dao.entity.Salle;
import com.example.concert_booking_api.dao.enums.RoleUtilisateur;
import com.example.concert_booking_api.dao.enums.StatutEvenement;
import com.example.concert_booking_api.dao.repository.CategoriePrixRepository;
import com.example.concert_booking_api.dao.repository.EvenementRepository;
import com.example.concert_booking_api.dao.repository.PlaceRepository;
import com.example.concert_booking_api.dao.repository.SalleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class EvenementService {

    private final EvenementRepository evenementRepository;
    private final SalleRepository salleRepository;
    private final CategoriePrixRepository categoriePrixRepository;
    private final PlaceRepository placeRepository;

    public EvenementService(
            EvenementRepository evenementRepository,
            SalleRepository salleRepository,
            CategoriePrixRepository categoriePrixRepository,
            PlaceRepository placeRepository
    ) {
        this.evenementRepository = evenementRepository;
        this.salleRepository = salleRepository;
        this.categoriePrixRepository = categoriePrixRepository;
        this.placeRepository = placeRepository;
    }
public Evenement creerEvenement(
        String titre,
        String description,
        String imageBannerUrl,
        Long salleId,
        OffsetDateTime dateOuvertureVentes,
        OffsetDateTime dateEvenement,
        RoleUtilisateur roleUtilisateur
) {

    verifierDroitOrganisateur(roleUtilisateur);

    verifierTitre(titre);

    verifierDateEvenementPourCreation(dateEvenement);
    verifierDateOuvertureVentes(dateOuvertureVentes, dateEvenement);

    Salle salle = trouverSalle(salleId);

    Evenement evenement = new Evenement();
    evenement.setTitre(titre.trim());
    evenement.setDescription(description != null ? description.trim() : null);
    evenement.setImageBannerUrl(imageBannerUrl != null ? imageBannerUrl.trim() : null);
    evenement.setSalle(salle);
    evenement.setDateOuvertureVentes(dateOuvertureVentes);
    evenement.setDateEvenement(dateEvenement);
    evenement.setStatut(StatutEvenement.BROUILLON);

    return evenementRepository.save(evenement);
}






public Evenement modifierEvenement(
        Long evenementId,
        Long salleId,
        String titre,
        String description,
        String imageBannerUrl,
        OffsetDateTime dateOuvertureVentes,
        OffsetDateTime dateEvenement,
        RoleUtilisateur roleUtilisateur
) {

         verifierDroitOrganisateur(roleUtilisateur);


    Evenement evenement = trouverEvenement(evenementId);

    verifierTitre(titre);
    verifierDateEvenementPourCreation(dateEvenement);
    verifierDateOuvertureVentes(dateOuvertureVentes, dateEvenement);

    Salle salle = trouverSalle(salleId);

    evenement.setTitre(titre.trim());
    evenement.setDescription(description != null ? description.trim() : null);
    evenement.setImageBannerUrl(imageBannerUrl != null ? imageBannerUrl.trim() : null);
    evenement.setSalle(salle);
    evenement.setDateOuvertureVentes(dateOuvertureVentes);
    evenement.setDateEvenement(dateEvenement);

    return evenementRepository.save(evenement);
}




 public Evenement publierEvenement(
        Long evenementId,
        RoleUtilisateur roleUtilisateur
) {

    verifierDroitPublication(roleUtilisateur);

    Evenement evenement =
            trouverEvenement(evenementId);

    verifierConditionsPublication(evenement);

    evenement.setStatut(
            StatutEvenement.PUBLIE
    );

    return evenementRepository.save(evenement);
}





    public Evenement changerStatut(
            Long evenementId,
            StatutEvenement nouveauStatut,
            RoleUtilisateur roleUtilisateur
    ) {

        verifierDroitPublication(roleUtilisateur);

        Evenement evenement =
                trouverEvenement(evenementId);

        if (nouveauStatut == null) {
            throw new BusinessException(
                    "Le nouveau statut est obligatoire."
            );
        }

        StatutEvenement ancienStatut =
                evenement.getStatut();

        verifierTransitionStatut(
                ancienStatut,
                nouveauStatut
        );

        if (nouveauStatut == StatutEvenement.PUBLIE) {
            verifierConditionsPublication(evenement);
        }

        if (nouveauStatut == StatutEvenement.TERMINE) {
            verifierFinEvenement(evenement);
        }

        if (nouveauStatut == StatutEvenement.ANNULE) {
            verifierAnnulation(evenement);
        }

        evenement.setStatut(nouveauStatut);

        return evenementRepository.save(evenement);
    }

   




 public Evenement annulerEvenement(
        Long evenementId,
        RoleUtilisateur roleUtilisateur
) {

    verifierDroitPublication(roleUtilisateur);

    Evenement evenement =
            trouverEvenement(evenementId);

    verifierAnnulation(evenement);

    evenement.setStatut(
            StatutEvenement.ANNULE
    );

    return evenementRepository.save(evenement);
}







    @Transactional(readOnly = true)
    public Evenement trouverEvenement(
            Long evenementId
    ) {

        if (evenementId == null) {
            throw new BusinessException(
                    "L'identifiant de l'événement est obligatoire."
            );
        }

        return evenementRepository.findById(evenementId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Événement introuvable."
                        )
                );
    }

    @Transactional(readOnly = true)
    public List<Evenement> listerEvenements() {
        return evenementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Evenement> listerEvenementsPublies() {
        return evenementRepository.findByStatut(
                StatutEvenement.PUBLIE
        );
    }

    @Transactional(readOnly = true)
    public List<Evenement> listerEvenementsParSalle(
            Long salleId
    ) {

        if (salleId == null) {
            throw new BusinessException(
                    "L'identifiant de la salle est obligatoire."
            );
        }

        return evenementRepository.findBySalleId(salleId);
    }

    private boolean possedeDesPlaces(
            Long evenementId
    ) {

        return !placeRepository
                .findByEvenementId(evenementId)
                .isEmpty();
    }

    private void verifierConditionsPublication(
            Evenement evenement
    ) {

        if (evenement.getStatut()
                != StatutEvenement.BROUILLON) {

            throw new BusinessException(
                    "Seul un événement en BROUILLON "
                            + "peut être publié."
            );
        }

        verifierTitre(
                evenement.getTitre()
        );

        if (evenement.getSalle() == null
                || evenement.getSalle().getId() == null
                || !salleRepository.existsById(
                        evenement.getSalle().getId()
                )) {

            throw new ResourceNotFoundException(
                    "La salle de l'événement "
                            + "n'existe plus."
            );
        }

        verifierDateEvenementPourPublication(
                evenement.getDateEvenement()
        );

        verifierDateOuvertureVentes(
                evenement.getDateOuvertureVentes(),
                evenement.getDateEvenement()
        );

        List<CategoriePrix> categories =
                categoriePrixRepository
                        .findByEvenementId(
                                evenement.getId()
                        );

        if (categories.isEmpty()) {

            throw new BusinessException(
                    "Impossible de publier l'événement : "
                            + "aucune catégorie de prix "
                            + "n'est configurée."
            );
        }

        if (!possedeDesPlaces(evenement.getId())) {

            throw new BusinessException(
                    "Impossible de publier l'événement : "
                            + "aucune place n'est configurée."
            );
        }
    }

    private void verifierTransitionStatut(
            StatutEvenement ancienStatut,
            StatutEvenement nouveauStatut
    ) {

        boolean transitionAutorisee =
                (ancienStatut == StatutEvenement.BROUILLON
                        && nouveauStatut == StatutEvenement.PUBLIE)

                || (ancienStatut == StatutEvenement.BROUILLON
                        && nouveauStatut == StatutEvenement.ANNULE)

                || (ancienStatut == StatutEvenement.PUBLIE
                        && nouveauStatut == StatutEvenement.ANNULE)

                || (ancienStatut == StatutEvenement.PUBLIE
                        && nouveauStatut == StatutEvenement.TERMINE);

        if (!transitionAutorisee) {

            throw new BusinessException(
                    "Transition de statut interdite : "
                            + ancienStatut
                            + " -> "
                            + nouveauStatut
            );
        }
    }

    private void verifierAnnulation(
            Evenement evenement
    ) {

        if (evenement.getStatut()
                == StatutEvenement.TERMINE) {

            throw new BusinessException(
                    "Un événement terminé "
                            + "ne peut pas être annulé."
            );
        }

        if (evenement.getStatut()
                == StatutEvenement.ANNULE) {

            throw new BusinessException(
                    "L'événement est déjà annulé."
            );
        }
    }

    private void verifierFinEvenement(
            Evenement evenement
    ) {

        if (evenement.getDateEvenement() == null) {

            throw new BusinessException(
                    "La date de l'événement "
                            + "est obligatoire."
            );
        }

        if (evenement.getDateEvenement()
                .isAfter(OffsetDateTime.now())) {

            throw new BusinessException(
                    "Un événement ne peut pas être "
                            + "terminé avant sa date."
            );
        }
    }

    private void verifierTitre(String titre) {

        if (titre == null
                || titre.trim().isEmpty()) {

            throw new BusinessException(
                    "Le titre de l'événement "
                            + "est obligatoire."
            );
        }
    }

    private void verifierDateEvenementPourCreation(
            OffsetDateTime dateEvenement
    ) {

        if (dateEvenement == null) {

            throw new BusinessException(
                    "La date de l'événement "
                            + "est obligatoire."
            );
        }

        if (!dateEvenement.isAfter(
                OffsetDateTime.now()
        )) {

            throw new BusinessException(
                    "La date de l'événement "
                            + "doit être dans le futur."
            );
        }
    }

    private void verifierDateEvenementPourPublication(
            OffsetDateTime dateEvenement
    ) {

        if (dateEvenement == null) {

            throw new BusinessException(
                    "La date de l'événement "
                            + "est obligatoire."
            );
        }

        if (!dateEvenement.isAfter(
                OffsetDateTime.now()
        )) {

            throw new BusinessException(
                    "La date de l'événement "
                            + "doit encore être dans le futur."
            );
        }
    }

    private void verifierDateOuvertureVentes(
            OffsetDateTime dateOuvertureVentes,
            OffsetDateTime dateEvenement
    ) {

        if (dateOuvertureVentes == null) {
            return;
        }

        if (dateEvenement == null) {

            throw new BusinessException(
                    "La date de l'événement "
                            + "est obligatoire."
            );
        }

        if (!dateOuvertureVentes.isBefore(
                dateEvenement
        )) {

            throw new BusinessException(
                    "La date d'ouverture des ventes "
                            + "doit être avant la date "
                            + "de l'événement."
            );
        }
    }

    private void verifierDroitOrganisateur(
            RoleUtilisateur roleUtilisateur
    ) {

        if (roleUtilisateur != RoleUtilisateur.ORGANISATEUR
                && roleUtilisateur != RoleUtilisateur.ADMIN) {

            throw new BusinessException(
                    "Seuls un ORGANISATEUR ou un ADMIN "
                            + "peuvent gérer les événements."
            );
        }
    }

    private void verifierDroitPublication(
            RoleUtilisateur roleUtilisateur
    ) {

        if (roleUtilisateur != RoleUtilisateur.ORGANISATEUR
                && roleUtilisateur != RoleUtilisateur.ADMIN) {

            throw new BusinessException(
                    "Seuls un ORGANISATEUR ou un ADMIN "
                            + "peuvent modifier le statut "
                            + "d'un événement."
            );
        }
    }

    private Salle trouverSalle(
            Long salleId
    ) {

        if (salleId == null) {

            throw new BusinessException(
                    "La salle est obligatoire."
            );
        }

        return salleRepository.findById(salleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Salle introuvable."
                        )
                );
    }
}
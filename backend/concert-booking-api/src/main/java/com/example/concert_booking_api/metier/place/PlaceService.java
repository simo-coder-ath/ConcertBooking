package com.example.concert_booking_api.metier.place;

import com.example.concert_booking_api.core.exception.BusinessException;
import com.example.concert_booking_api.core.exception.ResourceNotFoundException;
import com.example.concert_booking_api.dao.entity.CategoriePrix;
import com.example.concert_booking_api.dao.entity.Evenement;
import com.example.concert_booking_api.dao.entity.Place;
import com.example.concert_booking_api.dao.entity.Salle;
import com.example.concert_booking_api.dao.entity.Siege;
import com.example.concert_booking_api.dao.enums.StatutPlace;
import com.example.concert_booking_api.dao.repository.CategoriePrixRepository;
import com.example.concert_booking_api.dao.repository.EvenementRepository;
import com.example.concert_booking_api.dao.repository.PlaceRepository;
import com.example.concert_booking_api.dao.repository.SiegeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PlaceService {

    private final PlaceRepository placeRepository;
    private final EvenementRepository evenementRepository;
    private final SiegeRepository siegeRepository;
    private final CategoriePrixRepository categoriePrixRepository;

    public PlaceService(
            PlaceRepository placeRepository,
            EvenementRepository evenementRepository,
            SiegeRepository siegeRepository,
            CategoriePrixRepository categoriePrixRepository
    ) {
        this.placeRepository = placeRepository;
        this.evenementRepository = evenementRepository;
        this.siegeRepository = siegeRepository;
        this.categoriePrixRepository = categoriePrixRepository;
    }

    public Place creerPlace(
            Long evenementId,
            Long siegeId,
            Long categoriePrixId
    ) {

        Evenement evenement =
                trouverEvenement(evenementId);

        Siege siege =
                trouverSiege(siegeId);

        CategoriePrix categoriePrix =
                trouverCategoriePrix(categoriePrixId);

        verifierSiegeDansSalle(
                siege,
                evenement
        );

        verifierCategorieDansEvenement(
                categoriePrix,
                evenement
        );

        List<Place> placesExistantes =
                placeRepository.findByEvenementId(
                        evenementId
                );

        boolean siegeDejaUtilise =
                placesExistantes.stream()
                        .anyMatch(place ->
                                place.getSiege() != null
                                        && place.getSiege()
                                                .getId()
                                                .equals(siegeId)
                        );

        if (siegeDejaUtilise) {

            throw new BusinessException(
                    "Ce siège est déjà associé à une place "
                            + "pour cet événement."
            );
        }

        Place place = new Place();

        place.setEvenement(evenement);

        place.setSiege(siege);

        place.setCategoriePrix(categoriePrix);

        place.setStatut(
                StatutPlace.DISPONIBLE
        );

        place.setVersion(0);

        return placeRepository.save(place);
    }

    @Transactional(readOnly = true)
    public Place trouverPlace(
            Long placeId
    ) {

        if (placeId == null) {

            throw new BusinessException(
                    "L'identifiant de la place "
                            + "est obligatoire."
            );
        }

        return placeRepository.findById(
                placeId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Place introuvable."
                )
        );
    }

    public Place trouverPlacePourModification(
            Long placeId
    ) {

        if (placeId == null) {

            throw new BusinessException(
                    "L'identifiant de la place "
                            + "est obligatoire."
            );
        }

        return placeRepository
                .findByIdForUpdate(placeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Place introuvable."
                        )
                );
    }

    public void verifierDisponible(
            Place place
    ) {

        if (place == null) {

            throw new BusinessException(
                    "La place est obligatoire."
            );
        }

        if (place.getStatut()
                != StatutPlace.DISPONIBLE) {

            throw new BusinessException(
                    "La place "
                            + place.getId()
                            + " n'est pas disponible."
            );
        }
    }

    public Place verrouillerPlace(
            Long placeId
    ) {

        Place place =
                trouverPlacePourModification(
                        placeId
                );

        verifierTransition(
                place.getStatut(),
                StatutPlace.VERROUILLEE
        );

        place.setStatut(
                StatutPlace.VERROUILLEE
        );

        return placeRepository.save(place);
    }

    public Place reserverPlace(
            Long placeId
    ) {

        Place place =
                trouverPlacePourModification(
                        placeId
                );

        verifierTransition(
                place.getStatut(),
                StatutPlace.RESERVEE
        );

        place.setStatut(
                StatutPlace.RESERVEE
        );

        return placeRepository.save(place);
    }

    public Place vendrePlace(
            Long placeId
    ) {

        Place place =
                trouverPlacePourModification(
                        placeId
                );

        verifierTransition(
                place.getStatut(),
                StatutPlace.VENDUE
        );

        place.setStatut(
                StatutPlace.VENDUE
        );

        return placeRepository.save(place);
    }

    public Place libererPlace(
            Long placeId
    ) {

        Place place =
                trouverPlacePourModification(
                        placeId
                );

        if (place.getStatut()
                != StatutPlace.VERROUILLEE) {

            throw new BusinessException(
                    "Seule une place VERROUILLEE "
                            + "peut être libérée par expiration."
            );
        }

        place.setStatut(
                StatutPlace.DISPONIBLE
        );

        return placeRepository.save(place);
    }

    @Transactional(readOnly = true)
    public List<Place> listerPlacesParEvenement(
            Long evenementId
    ) {

        if (evenementId == null) {

            throw new BusinessException(
                    "L'identifiant de l'événement "
                            + "est obligatoire."
            );
        }

        return placeRepository.findByEvenementId(
                evenementId
        );
    }

    @Transactional(readOnly = true)
    public List<Place> listerPlacesDisponibles(
            Long evenementId
    ) {

        if (evenementId == null) {

            throw new BusinessException(
                    "L'identifiant de l'événement "
                            + "est obligatoire."
            );
        }

        return placeRepository
                .findByEvenementIdAndStatut(
                        evenementId,
                        StatutPlace.DISPONIBLE
                );
    }

    @Transactional(readOnly = true)
    public List<Place> listerPlacesParCategorie(
            Long evenementId,
            Long categoriePrixId
    ) {

        if (evenementId == null) {

            throw new BusinessException(
                    "L'identifiant de l'événement "
                            + "est obligatoire."
            );
        }

        if (categoriePrixId == null) {

            throw new BusinessException(
                    "L'identifiant de la catégorie de prix "
                            + "est obligatoire."
            );
        }

        return placeRepository
                .findByEvenementIdAndCategoriePrixId(
                        evenementId,
                        categoriePrixId
                );
    }

    private void verifierTransition(
            StatutPlace ancienStatut,
            StatutPlace nouveauStatut
    ) {

        boolean transitionAutorisee =
                (ancienStatut == StatutPlace.DISPONIBLE
                        && nouveauStatut
                        == StatutPlace.VERROUILLEE)

                || (ancienStatut
                        == StatutPlace.VERROUILLEE
                        && nouveauStatut
                        == StatutPlace.RESERVEE)

                || (ancienStatut
                        == StatutPlace.RESERVEE
                        && nouveauStatut
                        == StatutPlace.VENDUE);

        if (!transitionAutorisee) {

            throw new BusinessException(
                    "Transition de place interdite : "
                            + ancienStatut
                            + " -> "
                            + nouveauStatut
            );
        }
    }

    private void verifierSiegeDansSalle(
            Siege siege,
            Evenement evenement
    ) {

        if (siege == null
                || evenement == null) {

            throw new BusinessException(
                    "Le siège ou l'événement "
                            + "est invalide."
            );
        }

        Salle salleEvenement =
                evenement.getSalle();

        Salle salleSiege =
                siege.getSalle();

        if (salleEvenement == null
                || salleSiege == null
                || salleEvenement.getId() == null
                || salleSiege.getId() == null) {

            throw new BusinessException(
                    "Le siège ou la salle est invalide."
            );
        }

        if (!salleEvenement.getId()
                .equals(salleSiege.getId())) {

            throw new BusinessException(
                    "Le siège n'appartient pas "
                            + "à la salle de cet événement."
            );
        }
    }

    private void verifierCategorieDansEvenement(
            CategoriePrix categoriePrix,
            Evenement evenement
    ) {

        if (categoriePrix == null
                || evenement == null
                || evenement.getId() == null) {

            throw new BusinessException(
                    "La catégorie de prix "
                            + "ou l'événement est invalide."
            );
        }

        if (categoriePrix.getEvenement() == null
                || categoriePrix.getEvenement().getId() == null
                || !categoriePrix.getEvenement()
                        .getId()
                        .equals(evenement.getId())) {

            throw new BusinessException(
                    "La catégorie de prix "
                            + "n'appartient pas à cet événement."
            );
        }
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

    private Siege trouverSiege(
            Long siegeId
    ) {

        if (siegeId == null) {

            throw new BusinessException(
                    "L'identifiant du siège "
                            + "est obligatoire."
            );
        }

        return siegeRepository.findById(
                siegeId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Siège introuvable."
                )
        );
    }

    private CategoriePrix trouverCategoriePrix(
            Long categoriePrixId
    ) {

        if (categoriePrixId == null) {

            throw new BusinessException(
                    "La catégorie de prix "
                            + "est obligatoire."
            );
        }

        return categoriePrixRepository.findById(
                categoriePrixId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Catégorie de prix introuvable."
                )
        );
    }
}
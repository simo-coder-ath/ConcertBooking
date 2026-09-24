package com.example.concert_booking_api.metier.place;

import com.example.concert_booking_api.dao.entity.CategoriePrix;
import com.example.concert_booking_api.dao.entity.Evenement;
import com.example.concert_booking_api.dao.entity.Place;
import com.example.concert_booking_api.dao.entity.Salle;
import com.example.concert_booking_api.dao.entity.Siege;
import com.example.concert_booking_api.dao.enums.StatutEvenement;
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



        Evenement evenement = trouverEvenement(evenementId);


        Siege siege = trouverSiege(siegeId);


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
                placeRepository.findByEvenementId(evenementId);

        boolean siegeDejaUtilise =
                placesExistantes.stream()
                        .anyMatch(place ->
                                place.getSiege().getId().equals(siegeId)
                        );




        if (siegeDejaUtilise) {
            throw new IllegalStateException(
                    "Ce siège est déjà associé à une place "
                            + "pour cet événement."
            );


        }



        Place place = new Place();



        place.setEvenement(evenement);


        place.setSiege(siege);


        place.setCategoriePrix(categoriePrix);


        place.setStatut(StatutPlace.DISPONIBLE);


        place.setVersion(0);




        return placeRepository.save(place);








    }






    
    @Transactional(readOnly = true)
    public Place trouverPlace(Long placeId) {

        if (placeId == null) {
            throw new IllegalArgumentException(
                    "L'identifiant de la place est obligatoire."
            );
        }

        return placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Place introuvable."
                ));
    }



   
    public Place trouverPlacePourModification(Long placeId) {

        if (placeId == null) {
            throw new IllegalArgumentException(
                    "L'identifiant de la place est obligatoire."
            );
        }

        return placeRepository.findByIdForUpdate(placeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Place introuvable."
                ));
    }










    public void verifierDisponible(Place place) {

        if (place.getStatut() != StatutPlace.DISPONIBLE) {
            throw new IllegalStateException(
                    "La place "
                            + place.getId()
                            + " n'est pas disponible."
            );
        }
    }






    public Place verrouillerPlace(Long placeId) {



        Place place = trouverPlacePourModification(placeId);



        verifierTransition(
                place.getStatut(),
                StatutPlace.VERROUILLEE
        );



        place.setStatut(StatutPlace.VERROUILLEE);




        return placeRepository.save(place);


    }







  
    public Place reserverPlace(Long placeId) {



        Place place = trouverPlacePourModification(placeId);



        verifierTransition(
                place.getStatut(),
                StatutPlace.RESERVEE
        );



        place.setStatut(StatutPlace.RESERVEE);



        return placeRepository.save(place);





    }






    public Place vendrePlace(Long placeId) {



        Place place = trouverPlacePourModification(placeId);



        verifierTransition(
                place.getStatut(),
                StatutPlace.VENDUE
        );




        place.setStatut(StatutPlace.VENDUE);



        return placeRepository.save(place);





    }









    public Place libererPlace(Long placeId) {



        Place place = trouverPlacePourModification(placeId);



        if (place.getStatut() != StatutPlace.VERROUILLEE) {


            throw new IllegalStateException(
                    "Seule une place VERROUILLEE peut être libérée "
                            + "par expiration."
            );


        }




        place.setStatut(StatutPlace.DISPONIBLE);




        return placeRepository.save(place);
    }







    private void verifierTransition(
            StatutPlace ancienStatut,
            StatutPlace nouveauStatut
    ) {

        boolean transitionAutorisee =
                (ancienStatut == StatutPlace.DISPONIBLE
                        && nouveauStatut == StatutPlace.VERROUILLEE)

                || (ancienStatut == StatutPlace.VERROUILLEE
                        && nouveauStatut == StatutPlace.RESERVEE)

                || (ancienStatut == StatutPlace.RESERVEE
                        && nouveauStatut == StatutPlace.VENDUE);

        if (!transitionAutorisee) {
            throw new IllegalStateException(
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



        Salle salleEvenement = evenement.getSalle();


        Salle salleSiege = siege.getSalle();




        if (salleEvenement == null || salleSiege == null) {
            throw new IllegalStateException(
                    "Le siège ou la salle est invalide."
            );
        }






        if (!salleEvenement.getId().equals(salleSiege.getId())) {
            throw new IllegalStateException(
                    "Le siège n'appartient pas à la salle "
                            + "de cet événement."



            );


        }
    }







    private void verifierCategorieDansEvenement(
            CategoriePrix categoriePrix,
            Evenement evenement
    ) {



        if (categoriePrix.getEvenement() == null
                || !categoriePrix.getEvenement()
                .getId()
                .equals(evenement.getId())) {

            throw new IllegalStateException(
                    "La catégorie de prix n'appartient pas "
                            + "à cet événement."
            );
        }
    }













    private Evenement trouverEvenement(Long evenementId) {

        if (evenementId == null) {
            throw new IllegalArgumentException(
                    "L'identifiant de l'événement est obligatoire."
            );
        }

        return evenementRepository.findById(evenementId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Événement introuvable."
                ));
    }








    private Siege trouverSiege(Long siegeId) {

        if (siegeId == null) {
            throw new IllegalArgumentException(
                    "L'identifiant du siège est obligatoire."
            );
        }

        return siegeRepository.findById(siegeId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Siège introuvable."
                ));
    }

   



   
    private CategoriePrix trouverCategoriePrix(
            Long categoriePrixId
    ) {

        if (categoriePrixId == null) {
            throw new IllegalArgumentException(
                    "La catégorie de prix est obligatoire."
            );
        }

        return categoriePrixRepository.findById(categoriePrixId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Catégorie de prix introuvable."
                ));
    }
}

package com.example.concert_booking_api.metier.siege;

import com.example.concert_booking_api.dao.entity.Place;
import com.example.concert_booking_api.dao.entity.Salle;
import com.example.concert_booking_api.dao.entity.Siege;
import com.example.concert_booking_api.dao.enums.StatutPlace;
import com.example.concert_booking_api.dao.repository.PlaceRepository;
import com.example.concert_booking_api.dao.repository.SalleRepository;
import com.example.concert_booking_api.dao.repository.SiegeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SiegeService {

    private final SiegeRepository siegeRepository;
    private final SalleRepository salleRepository;
    private final PlaceRepository placeRepository;

    public SiegeService(
            SiegeRepository siegeRepository,
            SalleRepository salleRepository,
            PlaceRepository placeRepository
    ) {
        this.siegeRepository = siegeRepository;
        this.salleRepository = salleRepository;
        this.placeRepository = placeRepository;
    }







    public Siege creerSiege(
            Long salleId,
            String rang,
            String numero,
            String zone
    ) {



        Salle salle = trouverSalle(salleId);

        verifierRangEtNumero(rang, numero);

        String rangNormalise = rang.trim();
        String numeroNormalise = numero.trim();

       
        Optional<Siege> siegeExistant =
                siegeRepository.findBySalleIdAndRangAndNumero(
                        salleId,
                        rangNormalise,
                        numeroNormalise
                );

        if (siegeExistant.isPresent()) {
            throw new IllegalStateException(
                    "Un siège avec le rang "
                            + rangNormalise
                            + " et le numéro "
                            + numeroNormalise
                            + " existe déjà dans cette salle."
            );
        }








        long nombreSiegesActuels =
                siegeRepository.findBySalleId(salleId).size();

        if (nombreSiegesActuels >= salle.getCapacite()) {
            throw new IllegalStateException(
                    "Impossible d'ajouter ce siège : "
                            + "la capacité maximale de la salle est atteinte."
            );


        }



        Siege siege = new Siege();

        siege.setSalle(salle);
        siege.setRang(rangNormalise);
        siege.setNumero(numeroNormalise);

        if (zone != null && !zone.trim().isEmpty()) {


            siege.setZone(zone.trim());


        } else {


            siege.setZone(null);
        }


        return siegeRepository.save(siege);
    }




    public Siege modifierSiege(


            Long siegeId,
            String rang,
            String numero,
            String zone


    ) {


        Siege siege = trouverSiege(siegeId);

        verifierRangEtNumero(rang, numero);

        String nouveauRang = rang.trim();
        String nouveauNumero = numero.trim();




        verifierModificationPossible(siege);




        Long salleId = siege.getSalle().getId();



        Optional<Siege> siegeExistant =
                siegeRepository.findBySalleIdAndRangAndNumero(
                        salleId,
                        nouveauRang,
                        nouveauNumero
                );




        if (siegeExistant.isPresent()

                && !siegeExistant.get().getId().equals(siegeId)) {


            throw new IllegalStateException(

                    "Un autre siège avec le même rang et "
                            + "numéro existe déjà dans cette salle."

            );

        }


        siege.setRang(nouveauRang);



        siege.setNumero(nouveauNumero);



        if (zone != null && !zone.trim().isEmpty()) {

            siege.setZone(zone.trim());

        } else {



            siege.setZone(null);


        }



        return siegeRepository.save(siege);



    }



    
    public void supprimerSiege(Long siegeId) {


        Siege siege = trouverSiege(siegeId);


        Optional<Place> place =
                placeRepository.findBySiegeId(siegeId);


        if (place.isPresent()) {


            StatutPlace statut = place.get().getStatut();


            if (statut == StatutPlace.VENDUE
                    || statut == StatutPlace.RESERVEE
                    || statut == StatutPlace.VERROUILLEE) {


                throw new IllegalStateException(
                        "Impossible de supprimer ce siège : "
                                + "il est utilisé par une place active."

                );


            }



         


            throw new IllegalStateException(
                    "Impossible de supprimer ce siège : "
                            + "il est associé à une place."
            );
        }



        siegeRepository.delete(siege);


    }




    @Transactional(readOnly = true)
    public Siege trouverSiege(Long siegeId) {


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







    @Transactional(readOnly = true)
    private Salle trouverSalle(Long salleId) {



        if (salleId == null) {

            throw new IllegalArgumentException(
                    "L'identifiant de la salle est obligatoire."
            );

        }


        return salleRepository.findById(salleId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Salle introuvable."
                ));



    }




    private void verifierRangEtNumero(
            String rang,
            String numero
    ) {



        if (rang == null || rang.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Le rang du siège est obligatoire."
            );


        }



        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Le numéro du siège est obligatoire."
            );
        }
    }




    

    private void verifierModificationPossible(
            Siege siege
    ) {



        Optional<Place> place =
                placeRepository.findBySiegeId(siege.getId());

        if (place.isEmpty()) {

            return;

        }

        StatutPlace statut = place.get().getStatut();


        if (statut == StatutPlace.VENDUE
                || statut == StatutPlace.RESERVEE
                || statut == StatutPlace.VERROUILLEE) {

            throw new IllegalStateException(
                    "Impossible de modifier ce siège : "
                            + "il est utilisé par une place active."
            );
        }

     


        throw new IllegalStateException(


                "Impossible de modifier ce siège : "
                        + "il est déjà associé à une place."

                        
        );
    }
}
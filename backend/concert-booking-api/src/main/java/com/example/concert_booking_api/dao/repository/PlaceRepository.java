package com.example.concert_booking_api.dao.repository;

import com.example.concert_booking_api.dao.entity.Place;
import com.example.concert_booking_api.dao.enums.StatutPlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByEvenementId(Long evenementId);

    List<Place> findByEvenementIdAndStatut(
            Long evenementId,
            StatutPlace statut
    );

    List<Place> findByEvenementIdAndCategoriePrixId(
            Long evenementId,
            Long categoriePrixId
    );









      Optional<Place> findBySiegeId(Long siegeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Place> findByIdForUpdate(Long id);


    
}
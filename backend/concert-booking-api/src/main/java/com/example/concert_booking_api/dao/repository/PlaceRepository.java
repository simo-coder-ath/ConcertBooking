package com.example.concert_booking_api.dao.repository;

import com.example.concert_booking_api.dao.entity.Place;
import com.example.concert_booking_api.dao.enums.StatutPlace;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
    @Query("SELECT p FROM Place p WHERE p.id = :id")
    Optional<Place> findByIdForUpdate(@Param("id") Long id);
}
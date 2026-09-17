package com.example.concert_booking_api.dao.repository;

import com.example.concert_booking_api.dao.entity.Evenement;
import com.example.concert_booking_api.dao.enums.StatutEvenement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {

    List<Evenement> findByStatut(StatutEvenement statut);

    List<Evenement> findBySalleId(Long salleId);
}
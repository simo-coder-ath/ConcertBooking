package com.example.concert_booking_api.dao.repository;

import com.example.concert_booking_api.dao.entity.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    List<LigneCommande> findByCommandeId(Long commandeId);

    Optional<LigneCommande> findByPlaceId(Long placeId);
}
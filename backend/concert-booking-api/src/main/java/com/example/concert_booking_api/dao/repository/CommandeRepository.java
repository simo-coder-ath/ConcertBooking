package com.example.concert_booking_api.dao.repository;

import com.example.concert_booking_api.dao.entity.Commande;
import com.example.concert_booking_api.dao.enums.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    Optional<Commande> findByReference(String reference);

    List<Commande> findByUtilisateurIdOrderByCreatedAtDesc(Long utilisateurId);

    List<Commande> findByStatutAndExpiresAtBefore(
            StatutCommande statut,
            OffsetDateTime date
    );


 



}
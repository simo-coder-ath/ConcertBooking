package com.example.concert_booking_job.dao.repository;

import com.example.concert_booking_job.dao.entity.Commande;
import com.example.concert_booking_job.dao.entity.StatutCommande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface CommandeRepository extends JpaRepository<Commande, Long> {

    // Correspond directement à l'index idx_commandes_timeout de votre BDD
    List<Commande> findByStatutAndExpiresAtBefore(
            StatutCommande statut,
            OffsetDateTime maintenant
    );
}
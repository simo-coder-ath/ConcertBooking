package com.example.concert_booking_job.dao.repository;

import com.example.concert_booking_job.dao.entity.LigneCommande;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LigneCommandeRepository extends JpaRepository<LigneCommande, Long> {

    List<LigneCommande> findByCommandeId(Long commandeId);
}
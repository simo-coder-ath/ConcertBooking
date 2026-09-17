package com.example.concert_booking_api.dao.repository;

import com.example.concert_booking_api.dao.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FactureRepository extends JpaRepository<Facture, Long> {

    Optional<Facture> findByCommandeId(Long commandeId);

    Optional<Facture> findByTransactionId(String transactionId);
}
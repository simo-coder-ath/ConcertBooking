package com.example.concert_booking_api.dao.repository;

import com.example.concert_booking_api.dao.entity.CategoriePrix;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriePrixRepository extends JpaRepository<CategoriePrix, Long> {

    List<CategoriePrix> findByEvenementId(Long evenementId);
}
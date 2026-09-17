package com.example.concert_booking_api.dao.repository;

import com.example.concert_booking_api.dao.entity.Salle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalleRepository extends JpaRepository<Salle, Long> {
}
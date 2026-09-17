package com.example.concert_booking_api.dao.repository;

import com.example.concert_booking_api.dao.entity.Siege;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SiegeRepository extends JpaRepository<Siege, Long> {

    List<Siege> findBySalleId(Long salleId);
}
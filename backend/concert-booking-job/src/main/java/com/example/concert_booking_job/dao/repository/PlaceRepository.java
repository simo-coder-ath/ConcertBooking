package com.example.concert_booking_job.dao.repository;

import com.example.concert_booking_job.dao.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
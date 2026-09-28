package com.example.concert_booking_job;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ConcertBookingJobApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConcertBookingJobApplication.class, args);
    }
}
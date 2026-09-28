# Concert Booking

A concert booking application developed with **Java, Spring Boot, and React**.

The project is primarily based on a Spring Boot backend organized into multiple services to manage users, events, bookings, orders, payments, and notifications.

---------------------------------------------------------------------------------------------

# Architecture


                         ┌──────────────────────┐
                         │      Frontend        │
                         │     React + Vite     │
                         └──────────┬───────────┘
                                    │
                                    │ HTTP / REST
                                    ▼
                    ┌───────────────────────────────┐
                    │       Concert Booking API     │
                    │          Spring Boot          │
                    │                               │
                    │  REST Controllers             │
                    │  Services métier              │
                    │  Spring Security / JWT        │
                    │  Repositories / JPA            │
                    └───────────────┬───────────────┘
                                    │
                 ┌──────────────────┼──────────────────┐
                 │                  │                  │
                 ▼                  ▼                  ▼
          ┌────────────┐     ┌──────────────┐   ┌──────────────┐
          │  Database  │     │   RabbitMQ   │   │      Job     │
          │            │     │              │   │  Spring Boot │
          └────────────┘     └──────┬───────┘   └──────────────┘
                                    │
                                    ▼
                           ┌──────────────────┐
                           │   Notification
                              Microservice
                           │   Spring Boot    │
                           └────────┬─────────┘
                                    │
                                    ▼
                                  Email




-----------------------------------------------------------------------------------------------


# Backend

The backend consists of three Spring Boot services:

### concert-booking-api

The main API of the application.

It handles:

* authentication and users;
* events;
* venues, seats, and places;
* bookings;
* orders;
* payments;
* communication with the database.

The code is mainly organized according to the following structure:


app/          Controllers and DTOs
core/         Configuration, security, and error handling
dao/          Entities, enums, and repositories
metier/       Business logic


### concert-booking-job

A service dedicated to automated tasks.

It notably contains `CommandeExpirationJob`, which handles order expiration and releases the associated seats.

### concert-booking-notification

A service dedicated to notifications.

It consumes events published through RabbitMQ, particularly when an order is paid, and then triggers the sending of a notification.

## Security

The API uses **Spring Security** with **JWT-based authentication**.

The main components are:


core/security/
├── CustomUserDetailsService.java
├── JwtAuthenticationFilter.java
├── JwtService.java
└── SecurityConfig.java


## Asynchronous Communication

**RabbitMQ** is used to decouple certain operations.

When an order is paid:



CommandeService
      |
      v
CommandeEventPublisher
      |
      v
RabbitMQ
      |
      v
CommandePayeeListener
      |
      v
EmailService



----------------------------------------------------------------------------------------------

# Installation

### Prerequisites

* Java
* Maven
* Node.js / npm
* Relational database
* RabbitMQ

### Clone the Project

git clone <https://github.com/simo-coder-ath/ConcertBooking>
cd concert-booking


### Configure the Database

Adjust the database settings in:

backend/concert-booking-api/src/main/resources/application.properties


The `job` and `notification` services also have their own configuration files.

### Run the API

cd backend/concert-booking-api
mvn spring-boot:run


### Run the Job Service

cd backend/concert-booking-job
mvn spring-boot:run


### Run the Notification Service


cd backend/concert-booking-notification
mvn spring-boot:run



-----------------------------------------------------------------------------------------------

# Frontend

The frontend is developed with **React + Vite** and primarily serves as the client interface for the API.

It is located in:

frontend/


To install and run it:

cd frontend
npm install
npm run dev



package com.example.concert_booking_api.dao.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "sieges",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_salle_siege",
            columnNames = {"salle_id", "rang", "numero"}
        )
    }
)
public class Siege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @Column(nullable = false, length = 10)
    private String rang;

    @Column(nullable = false, length = 10)
    private String numero;

    @Column(length = 50)
    private String zone;

    public Siege() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Salle getSalle() {
        return salle;
    }

    public void setSalle(Salle salle) {
        this.salle = salle;
    }

    public String getRang() {
        return rang;
    }

    public void setRang(String rang) {
        this.rang = rang;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }
}
package com.example.concert_booking_job.dao.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lignes_commande")
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commande_id", nullable = false)
    private Commande commande;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false, unique = true)
    private Place place;

    @Column(name = "prix_unitaire", nullable = false)
    private BigDecimal prixUnitaire;

    public Long getId() { return id; }
    public Commande getCommande() { return commande; }
    public Place getPlace() { return place; }
}

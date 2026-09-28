package com.example.concert_booking_job.dao.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "places")
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evenement_id", nullable = false)
    private Long evenementId;

    @Column(name = "siege_id", nullable = false)
    private Long siegeId;

    @Column(name = "categorie_prix_id", nullable = false)
    private Long categoriePrixId;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
  @Column(columnDefinition = "statut_place")
    private StatutPlace statut;

    // Verrouillage optimiste — correspond à la colonne "version" de votre table
    @Version
    @Column(name = "version", nullable = false)
    private Integer version;

    public Long getId() { return id; }
    public StatutPlace getStatut() { return statut; }
    public void setStatut(StatutPlace statut) { this.statut = statut; }
    public Integer getVersion() { return version; }
}
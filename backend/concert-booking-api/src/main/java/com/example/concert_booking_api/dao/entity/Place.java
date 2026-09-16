package com.example.concert_booking_api.dao.entity;

import com.example.concert_booking_api.dao.enums.StatutPlace;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(
    name = "places",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_evenement_siege",
            columnNames = {"evenement_id", "siege_id"}
        )
    }
)
public class Place {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evenement_id", nullable = false)
    private Evenement evenement;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "siege_id", nullable = false)
    private Siege siege;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categorie_prix_id", nullable = false)
    private CategoriePrix categoriePrix;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "statut_place")
    private StatutPlace statut = StatutPlace.DISPONIBLE;

    @Version
    @Column(nullable = false)
    private Integer version = 0;

    public Place() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public Siege getSiege() {
        return siege;
    }

    public void setSiege(Siege siege) {
        this.siege = siege;
    }

    public CategoriePrix getCategoriePrix() {
        return categoriePrix;
    }

    public void setCategoriePrix(CategoriePrix categoriePrix) {
        this.categoriePrix = categoriePrix;
    }

    public StatutPlace getStatut() {
        return statut;
    }

    public void setStatut(StatutPlace statut) {
        this.statut = statut;
    }

    public Integer getVersion() {
        return version;
    }
}
package com.example.concert_booking_api.dao.entity;

import com.example.concert_booking_api.dao.enums.StatutEvenement;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "evenements")
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "salle_id", nullable = false)
    private Salle salle;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_banner_url", length = 500)
    private String imageBannerUrl;

    @Column(name = "date_ouverture_ventes")
    private OffsetDateTime dateOuvertureVentes;

    @Column(name = "date_evenement", nullable = false)
    private OffsetDateTime dateEvenement;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "statut_evenement")
    private StatutEvenement statut = StatutEvenement.BROUILLON;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    public Evenement() {
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

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageBannerUrl() {
        return imageBannerUrl;
    }

    public void setImageBannerUrl(String imageBannerUrl) {
        this.imageBannerUrl = imageBannerUrl;
    }

    public OffsetDateTime getDateOuvertureVentes() {
        return dateOuvertureVentes;
    }

    public void setDateOuvertureVentes(OffsetDateTime dateOuvertureVentes) {
        this.dateOuvertureVentes = dateOuvertureVentes;
    }

    public OffsetDateTime getDateEvenement() {
        return dateEvenement;
    }

    public void setDateEvenement(OffsetDateTime dateEvenement) {
        this.dateEvenement = dateEvenement;
    }

    public StatutEvenement getStatut() {
        return statut;
    }

    public void setStatut(StatutEvenement statut) {
        this.statut = statut;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
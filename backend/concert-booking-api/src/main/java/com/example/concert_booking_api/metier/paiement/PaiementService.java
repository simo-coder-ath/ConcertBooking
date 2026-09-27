package com.example.concert_booking_api.metier.paiement;

import com.example.concert_booking_api.core.exception.BusinessException;
import com.example.concert_booking_api.core.exception.ResourceNotFoundException;
import com.example.concert_booking_api.dao.entity.Commande;
import com.example.concert_booking_api.dao.entity.Facture;
import com.example.concert_booking_api.dao.entity.LigneCommande;
import com.example.concert_booking_api.dao.entity.Place;
import com.example.concert_booking_api.dao.enums.StatutCommande;
import com.example.concert_booking_api.dao.enums.StatutPlace;
import com.example.concert_booking_api.dao.repository.CommandeRepository;
import com.example.concert_booking_api.dao.repository.FactureRepository;
import com.example.concert_booking_api.dao.repository.LigneCommandeRepository;
import com.example.concert_booking_api.dao.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
@Transactional
public class PaiementService {

    private final CommandeRepository commandeRepository;
    private final FactureRepository factureRepository;
    private final LigneCommandeRepository ligneCommandeRepository;
    private final PlaceRepository placeRepository;
    private final PaymentProviderClient paymentProviderClient;

    public PaiementService(
            CommandeRepository commandeRepository,
            FactureRepository factureRepository,
            LigneCommandeRepository ligneCommandeRepository,
            PlaceRepository placeRepository,
            PaymentProviderClient paymentProviderClient
    ) {
        this.commandeRepository = commandeRepository;
        this.factureRepository = factureRepository;
        this.ligneCommandeRepository = ligneCommandeRepository;
        this.placeRepository = placeRepository;
        this.paymentProviderClient = paymentProviderClient;
    }

    public Facture payerCommande(
            Long commandeId,
            Long utilisateurId,
            BigDecimal montantDemande,
            String paymentToken
    ) {

        Commande commande =
                trouverCommande(commandeId);

        verifierProprietaire(
                commande,
                utilisateurId
        );

        verifierCommandePayable(
                commande
        );

        verifierMontantDemande(
                commande,
                montantDemande
        );

        PaymentResult resultat =
                paymentProviderClient.confirmerPaiement(
                        paymentToken,
                        commande.getMontantTotal()
                );

        if (!resultat.succes()) {
            traiterEchecPaiement(
                    commande,
                    resultat
            );
        }

        verifierResultatPaiement(
                commande,
                resultat
        );

        return traiterPaiementConfirme(
                commande,
                resultat
        );
    }

    private void verifierProprietaire(
            Commande commande,
            Long utilisateurId
    ) {

        if (utilisateurId == null) {

            throw new BusinessException(
                    "L'identifiant utilisateur est obligatoire."
            );
        }

        if (commande.getUtilisateur() == null
                || commande.getUtilisateur().getId() == null
                || !commande.getUtilisateur()
                        .getId()
                        .equals(utilisateurId)) {

            throw new BusinessException(
                    "Cette commande n'appartient pas "
                            + "à cet utilisateur."
            );
        }
    }

    private void verifierCommandePayable(
            Commande commande
    ) {

        if (commande.getStatut()
                != StatutCommande.EN_ATTENTE_DE_PAIEMENT) {

            throw new BusinessException(
                    "Cette commande n'est plus "
                            + "en attente de paiement."
            );
        }

        if (commande.getExpiresAt() == null) {

            throw new BusinessException(
                    "La date d'expiration de la commande "
                            + "est invalide."
            );
        }

        if (!commande.getExpiresAt()
                .isAfter(OffsetDateTime.now())) {

            throw new BusinessException(
                    "La commande a expiré."
            );
        }
    }

    private void verifierMontantDemande(
            Commande commande,
            BigDecimal montantDemande
    ) {

        if (montantDemande == null) {

            throw new BusinessException(
                    "Le montant du paiement "
                            + "est obligatoire."
            );
        }

        if (commande.getMontantTotal() == null) {

            throw new BusinessException(
                    "Le montant de la commande "
                            + "est invalide."
            );
        }

        if (commande.getMontantTotal()
                .compareTo(montantDemande) != 0) {

            throw new BusinessException(
                    "Le montant du paiement ne correspond "
                            + "pas au montant de la commande."
            );
        }
    }

    private void verifierResultatPaiement(
            Commande commande,
            PaymentResult resultat
    ) {

        if (resultat == null) {

            throw new BusinessException(
                    "Le service de paiement n'a retourné "
                            + "aucun résultat."
            );
        }

        if (resultat.transactionId() == null
                || resultat.transactionId().isBlank()) {

            throw new BusinessException(
                    "Le provider n'a pas fourni "
                            + "d'identifiant de transaction."
            );
        }

        if (resultat.montant() == null
                || commande.getMontantTotal()
                        .compareTo(resultat.montant()) != 0) {

            throw new BusinessException(
                    "Le montant confirmé par le provider "
                            + "ne correspond pas à la commande."
            );
        }

        if (resultat.provider() == null
                || resultat.provider().isBlank()) {

            throw new BusinessException(
                    "Le provider de paiement est invalide."
            );
        }
    }

    private Facture traiterPaiementConfirme(
            Commande commande,
            PaymentResult resultat
    ) {

        Facture factureExistante =
                factureRepository
                        .findByTransactionId(
                                resultat.transactionId()
                        )
                        .orElse(null);

        if (factureExistante != null) {
            return factureExistante;
        }

        List<LigneCommande> lignes =
                ligneCommandeRepository
                        .findByCommandeId(
                                commande.getId()
                        );

        if (lignes.isEmpty()) {

            throw new BusinessException(
                    "La commande ne contient "
                            + "aucune place."
            );
        }

        for (LigneCommande ligne : lignes) {

            if (ligne.getPlace() == null
                    || ligne.getPlace().getId() == null) {

                throw new BusinessException(
                        "Une ligne de commande "
                                + "ne possède pas de place valide."
                );
            }

            Place place =
                    placeRepository
                            .findByIdForUpdate(
                                    ligne.getPlace().getId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Place introuvable : "
                                                    + ligne.getPlace()
                                                    .getId()
                                    )
                            );

            if (place.getStatut()
                    != StatutPlace.VERROUILLEE) {

                throw new BusinessException(
                        "La place "
                                + place.getId()
                                + " n'est plus verrouillée."
                );
            }

            place.setStatut(
                    StatutPlace.VENDUE
            );

            placeRepository.save(place);
        }

        commande.setStatut(
                StatutCommande.PAYEE
        );

        commandeRepository.save(commande);

        Facture facture = new Facture();

        facture.setCommande(commande);

        facture.setMontantRegle(
                resultat.montant()
        );

        facture.setPaymentProvider(
                resultat.provider()
        );

        facture.setTransactionId(
                resultat.transactionId()
        );

        facture.setStatutPaiement(
                "PAYE"
        );

        facture.setPdfTicketUrl(null);

        return factureRepository.save(facture);
    }

    private void traiterEchecPaiement(
            Commande commande,
            PaymentResult resultat
    ) {

        List<LigneCommande> lignes =
                ligneCommandeRepository
                        .findByCommandeId(
                                commande.getId()
                        );

        for (LigneCommande ligne : lignes) {

            if (ligne.getPlace() == null
                    || ligne.getPlace().getId() == null) {
                continue;
            }

            Place place =
                    placeRepository
                            .findByIdForUpdate(
                                    ligne.getPlace().getId()
                            )
                            .orElse(null);

            if (place == null) {
                continue;
            }

            if (place.getStatut()
                    == StatutPlace.VERROUILLEE) {

                place.setStatut(
                        StatutPlace.DISPONIBLE
                );

                placeRepository.save(place);
            }
        }

        commande.setStatut(
                StatutCommande.ECHOUEE
        );

        commandeRepository.save(commande);

        String message =
                resultat != null
                        && resultat.message() != null
                        && !resultat.message().isBlank()
                        ? resultat.message()
                        : "Le paiement a échoué.";

        throw new BusinessException(
                "Le paiement a échoué : " + message
        );
    }

    @Transactional(readOnly = true)
    private Commande trouverCommande(
            Long commandeId
    ) {

        if (commandeId == null) {

            throw new BusinessException(
                    "L'identifiant de la commande "
                            + "est obligatoire."
            );
        }

        return commandeRepository.findById(
                commandeId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Commande introuvable."
                )
        );
    }
}
package com.example.concert_booking_api.metier.utilisateur;

import com.example.concert_booking_api.core.exception.BusinessException;
import com.example.concert_booking_api.core.exception.ResourceNotFoundException;
import com.example.concert_booking_api.dao.entity.Utilisateur;
import com.example.concert_booking_api.dao.enums.RoleUtilisateur;
import com.example.concert_booking_api.dao.repository.CommandeRepository;
import com.example.concert_booking_api.dao.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final CommandeRepository commandeRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile(
                    "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
            );

    public UtilisateurService(
            UtilisateurRepository utilisateurRepository,
            CommandeRepository commandeRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.utilisateurRepository = utilisateurRepository;
        this.commandeRepository = commandeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Utilisateur creerUtilisateur(
            String email,
            String motDePasse,
            String nom,
            String prenom,
            String telephone
    ) {

        verifierEmail(email);
        verifierMotDePasse(motDePasse);
        verifierNomEtPrenom(nom, prenom);

        String emailNormalise =
                email.trim().toLowerCase();

        if (utilisateurRepository.existsByEmail(
                emailNormalise
        )) {

            throw new BusinessException(
                    "Cet email est déjà utilisé."
            );
        }

        Utilisateur utilisateur =
                new Utilisateur();

        utilisateur.setEmail(emailNormalise);

        utilisateur.setMotDePasse(
                passwordEncoder.encode(motDePasse)
        );

        utilisateur.setNom(
                nom.trim()
        );

        utilisateur.setPrenom(
                prenom.trim()
        );

        utilisateur.setTelephone(
        telephone != null
                ? telephone.trim()
                : null
);


        utilisateur.setRole(
                RoleUtilisateur.CLIENT
        );

        return utilisateurRepository.save(
                utilisateur
        );
    }

    public Utilisateur modifierUtilisateur(
            Long utilisateurId,
            String email,
            String nom,
            String prenom,
            String telephone
    ) {

        Utilisateur utilisateur =
                trouverUtilisateur(utilisateurId);

        verifierEmail(email);
        verifierNomEtPrenom(nom, prenom);

        String emailNormalise =
                email.trim().toLowerCase();

        Optional<Utilisateur> utilisateurAvecEmail =
                utilisateurRepository.findByEmail(
                        emailNormalise
                );

        if (utilisateurAvecEmail.isPresent()
                && !utilisateurAvecEmail
                        .get()
                        .getId()
                        .equals(utilisateurId)) {

            throw new BusinessException(
                    "Cet email est déjà utilisé "
                            + "par un autre utilisateur."
            );
        }

        utilisateur.setEmail(
                emailNormalise
        );

        utilisateur.setNom(
                nom.trim()
        );

        utilisateur.setPrenom(
                prenom.trim()
        );

        utilisateur.setTelephone(
                telephone != null
                        ? telephone.trim()
                        : null
        );

        return utilisateurRepository.save(
                utilisateur
        );
    }

    public Utilisateur modifierRole(
            Long utilisateurId,
            RoleUtilisateur nouveauRole,
            RoleUtilisateur roleUtilisateurConnecte
    ) {

        verifierDroitAdmin(
                roleUtilisateurConnecte
        );

        if (nouveauRole == null) {

            throw new BusinessException(
                    "Le nouveau rôle est obligatoire."
            );
        }

        Utilisateur utilisateur =
                trouverUtilisateur(utilisateurId);

        utilisateur.setRole(
                nouveauRole
        );

        return utilisateurRepository.save(
                utilisateur
        );
    }

    public void supprimerUtilisateur(
            Long utilisateurId
    ) {

        Utilisateur utilisateur =
                trouverUtilisateur(utilisateurId);

        boolean possedeDesCommandes =
                commandeRepository
                        .findByUtilisateurIdOrderByCreatedAtDesc(
                                utilisateurId
                        )
                        .stream()
                        .findAny()
                        .isPresent();

        if (possedeDesCommandes) {

            throw new BusinessException(
                    "Impossible de supprimer cet utilisateur "
                            + "car il possède des commandes."
            );
        }

        utilisateurRepository.delete(
                utilisateur
        );
    }

    @Transactional(readOnly = true)
    public Utilisateur trouverUtilisateur(
            Long utilisateurId
    ) {

        if (utilisateurId == null) {

            throw new BusinessException(
                    "L'identifiant utilisateur "
                            + "est obligatoire."
            );
        }

        return utilisateurRepository.findById(
                utilisateurId
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Utilisateur introuvable."
                )
        );
    }

    private void verifierDroitAdmin(
            RoleUtilisateur roleUtilisateurConnecte
    ) {

        if (roleUtilisateurConnecte
                != RoleUtilisateur.ADMIN) {

            throw new BusinessException(
                    "Cette opération est réservée "
                            + "aux administrateurs."
            );
        }
    }

    private void verifierEmail(
            String email
    ) {

        if (email == null
                || email.trim().isEmpty()) {

            throw new BusinessException(
                    "L'email est obligatoire."
            );
        }

        String emailNormalise =
                email.trim();

        if (!EMAIL_PATTERN
                .matcher(emailNormalise)
                .matches()) {

            throw new BusinessException(
                    "Le format de l'email est invalide."
            );
        }
    }

    private void verifierMotDePasse(
            String motDePasse
    ) {

        if (motDePasse == null
                || motDePasse.isBlank()) {

            throw new BusinessException(
                    "Le mot de passe est obligatoire."
            );
        }

        if (motDePasse.length() < 8) {

            throw new BusinessException(
                    "Le mot de passe doit contenir "
                            + "au moins 8 caractères."
            );
        }

        if (!motDePasse.matches(
                ".*[A-Z].*"
        )) {

            throw new BusinessException(
                    "Le mot de passe doit contenir "
                            + "au moins une majuscule."
            );
        }

        if (!motDePasse.matches(
                ".*[a-z].*"
        )) {

            throw new BusinessException(
                    "Le mot de passe doit contenir "
                            + "au moins une minuscule."
            );
        }

        if (!motDePasse.matches(
                ".*\\d.*"
        )) {

            throw new BusinessException(
                    "Le mot de passe doit contenir "
                            + "au moins un chiffre."
            );
        }

        if (!motDePasse.matches(
                ".*[^A-Za-z0-9].*"
        )) {

            throw new BusinessException(
                    "Le mot de passe doit contenir "
                            + "au moins un caractère spécial."
            );
        }
    }

    private void verifierNomEtPrenom(
            String nom,
            String prenom
    ) {

        if (nom == null
                || nom.trim().isEmpty()) {

            throw new BusinessException(
                    "Le nom est obligatoire."
            );
        }

        if (prenom == null
                || prenom.trim().isEmpty()) {

            throw new BusinessException(
                    "Le prénom est obligatoire."
            );
        }
    }
}
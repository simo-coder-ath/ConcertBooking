package com.example.concert_booking_api.core.security;

import com.example.concert_booking_api.dao.entity.Utilisateur;
import com.example.concert_booking_api.dao.repository.UtilisateurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService
        implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService(
            UtilisateurRepository utilisateurRepository
    ) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(
            String email
    ) {

        if (email == null || email.isBlank()) {
            throw new UsernameNotFoundException(
                    "Email utilisateur obligatoire."
            );
        }

        String emailNormalise =
                email.trim().toLowerCase();

        Utilisateur utilisateur =
                utilisateurRepository
                        .findByEmail(emailNormalise)
                        .orElseThrow(() ->
                                new UsernameNotFoundException(
                                        "Utilisateur introuvable."
                                )
                        );

        if (utilisateur.getRole() == null) {
            throw new UsernameNotFoundException(
                    "Le rôle de l'utilisateur est invalide."
            );
        }

        return User.builder()
                .username(utilisateur.getEmail())
                .password(utilisateur.getMotDePasse())
                .authorities(
                        new SimpleGrantedAuthority(
                                "ROLE_"
                                        + utilisateur
                                                .getRole()
                                                .name()
                        )
                )
                .build();
    }
}
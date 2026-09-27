package com.example.concert_booking_api.core.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService userDetailsService;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CustomUserDetailsService userDetailsService,
            CorsConfigurationSource corsConfigurationSource
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(
                        userDetailsService
                );

        provider.setPasswordEncoder(
                passwordEncoder()
        );

        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource
                        )
                )

                .csrf(csrf ->
                        csrf.disable()
                )

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authenticationProvider(
                        authenticationProvider()
                )

                .authorizeHttpRequests(auth -> auth

                        // ==============================
                        // ROUTES PUBLIQUES
                        // ==============================

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // Lecture des événements : publique
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/evenements/**"
                        ).permitAll()

                        // ==============================
                        // ÉCRITURE ÉVÉNEMENTS : ORGANISATEUR / ADMIN
                        // ==============================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/evenements/**"
                        ).hasAnyRole("ORGANISATEUR", "ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/evenements/**"
                        ).hasAnyRole("ORGANISATEUR", "ADMIN")

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/evenements/**"
                        ).hasAnyRole("ORGANISATEUR", "ADMIN")

                        // ==============================
                        // ADMIN
                        // ==============================

                        .requestMatchers(
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // ==============================
                        // ORGANISATEUR
                        // ==============================

                        .requestMatchers(
                                "/api/organisateur/**"
                        ).hasAnyRole(
                                "ORGANISATEUR",
                                "ADMIN"
                        )

                        // ==============================
                        // RESTE DE L'API
                        // ==============================

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
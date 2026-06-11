package com.kode_project.ebooking.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
public class SecurityConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Autowired
    AuthenticationManager authenticationManager;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration cors = new CorsConfiguration();
                        cors.setAllowedOriginPatterns(Collections.singletonList("http://localhost*")); // utile dev et prod
                        cors.setAllowedMethods(Collections.singletonList("*"));
                        cors.setAllowedHeaders(Collections.singletonList("*"));
                        cors.setExposedHeaders(Collections.singletonList("Authorization"));
                        cors.setAllowCredentials(true);
                        return cors;
                    }
                }))
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/login", "/api/users/registration/**", "/verifyEmail/**").permitAll()
                        .requestMatchers(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html"
                        ).permitAll()
                        // ADMIN seulement
                        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/{id}/bloquer").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/users/{id}/activer").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/statistiques").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/services").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/services/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/services/{id}").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/prestataires").hasAuthority("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/prestataires/{id}").hasAuthority("ADMIN")

                        // PRO seulement
                        .requestMatchers(HttpMethod.PUT, "/api/prestataires/{id}").hasAuthority("PRO")
                        .requestMatchers(HttpMethod.POST, "/api/disponibilites").hasAuthority("PRO")
                        .requestMatchers(HttpMethod.PUT, "/api/disponibilites/{id}").hasAuthority("PRO")
                        .requestMatchers(HttpMethod.DELETE, "/api/disponibilites/{id}").hasAuthority("PRO")
                        .requestMatchers(HttpMethod.GET, "/api/rendez-vous/prestataire/{id}").hasAuthority("PRO")
                        .requestMatchers(HttpMethod.PATCH, "/api/rendez-vous/{id}").hasAuthority("PRO")

                        // CLIENT seulement
                        .requestMatchers(HttpMethod.POST, "/api/rendez-vous").hasAuthority("CLIENT")
                        .requestMatchers(HttpMethod.DELETE, "/api/rendez-vous/{id}").hasAuthority("CLIENT")
                        .requestMatchers(HttpMethod.GET, "/api/rendez-vous/client/{id}").hasAuthority("CLIENT")

                        // PRO et CLIENT
                        .requestMatchers(HttpMethod.GET, "/api/disponibilites/prestataire/{prestataireId}").hasAnyAuthority("PRO", "CLIENT")
                        // Authentifié (tous les rôles)
                        .requestMatchers(HttpMethod.GET, "/api/auth/me").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/users/{id}").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/users/{id}").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/users/{id}/password").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/prestataires").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/prestataires/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/services").authenticated()

                        .anyRequest().authenticated()

                )
                .addFilterBefore(new JWTAuthenticationFilter(authenticationManager, jwtSecret), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTAuthorizationFilter(jwtSecret), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}

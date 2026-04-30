package com.agriserve.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration:
 * - Stateless session (JWT-based)
 * - Role-based access control via method-level annotations
 * - BCrypt password encoding
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthEntryPoint jwtAuthEntryPoint;

    // ─── Public (no auth required) endpoints ─────────────────────────────────
    private static final String[] PUBLIC_URLS = {
            "/auth/**",
            "/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/actuator/health",
            "/actuator/info"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF (stateless JWT — no sessions)
            .csrf(AbstractHttpConfigurer::disable)

            // Stateless session management
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Custom 401 entry point
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthEntryPoint))

            // Authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(PUBLIC_URLS).permitAll()

                // Admin — full access
                .requestMatchers("/users/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                // Advisory content management — officers & above
                .requestMatchers(HttpMethod.POST, "/advisory/content/**").hasAnyRole("EXTENSION_OFFICER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/advisory/content/**").hasAnyRole("EXTENSION_OFFICER", "ADMIN")

                // Reports — managers & auditors
                .requestMatchers("/reports/**").hasAnyRole("PROGRAM_MANAGER", "ADMIN", "GOVERNMENT_AUDITOR")

                // Audits — compliance officer & government auditor
                .requestMatchers("/compliance/audits/**").hasAnyRole("COMPLIANCE_OFFICER", "GOVERNMENT_AUDITOR", "ADMIN")

                // Everything else — authenticated only
                .anyRequest().authenticated()
            );

        // Register JWT filter before Spring's default username/password filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

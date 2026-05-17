package com.kikesoft.moviesapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Minimal security configuration for issue-41.
 * Keeps all endpoints open while enabling Security beans required by the ticket.
 */
@Configuration
public class SecurityConfig {

    /**
     * Configures a minimal open security chain.
     *
     * @param http the HTTP security builder
     * @return the configured security filter chain
     * @throws Exception when security configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    /**
     * Provides password encoder bean for credential hashing.
     *
     * @return password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Exposes authentication manager bean using Spring configuration.
     * Method name must remain authenticationManage() per ticket requirement.
     *
     * @param authConfig authentication configuration
     * @return authentication manager
     * @throws Exception when manager cannot be resolved
     */
    @Bean
    public AuthenticationManager authenticationManage(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}

package com.kikesoft.moviesapi.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global Spring MVC CORS configuration driven by application properties.
 *
 * @author Enrique Sanchez
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    /**
     * Comma-separated list of allowed origins.
     */
    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    /**
     * Whether cross-origin requests can include credentials.
     */
    @Value("${app.cors.allow-credentials}")
    private boolean allowCredentials;

    /**
     * Registers global CORS mappings for API routes.
     *
     * @param registry MVC CORS registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);

        CorsRegistration corsRegistration = registry.addMapping("/movies/**")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization", "Accept", "Origin", "X-Requested-With")
                .exposedHeaders("Location")
                .maxAge(3600);

        if (origins.length > 0) {
            corsRegistration.allowedOrigins(origins);
        }

        corsRegistration.allowCredentials(allowCredentials);
    }
}

package com.kikesoft.moviesapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

/**
 * OpenAPI configuration that maps API metadata from application properties.
 *
 * @author Enrique Sanchez
 */
@Configuration
public class OpenApiConfig {

    /**
     * API title shown in Swagger/OpenAPI docs.
     */
    @Value("${api.docs.title}")
    private String title;

    /**
     * API description shown in Swagger/OpenAPI docs.
     */
    @Value("${api.docs.description}")
    private String description;

    /**
     * API version shown in Swagger/OpenAPI docs.
     */
    @Value("${api.docs.version}")
    private String version;

    /**
     * Contact name shown in API docs.
     */
    @Value("${api.docs.contact.name}")
    private String contactName;

    /**
     * Contact email shown in API docs.
     */
    @Value("${api.docs.contact.email}")
    private String contactEmail;

    /**
     * Contact URL shown in API docs.
     */
    @Value("${api.docs.contact.url}")
    private String contactUrl;

    /**
     * Builds the OpenAPI specification metadata for Swagger UI and API docs.
     *
     * @return configured OpenAPI instance
     */
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(title)
                        .description(description)
                        .version(version)
                        .contact(new Contact()
                                .name(contactName)
                                .email(contactEmail)
                                .url(contactUrl)));
    }
}
package com.kikesoft.moviesapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

    @Value("${api.docs.title}")
    private String title;

    @Value("${api.docs.description}")
    private String description;

    @Value("${api.docs.version}")
    private String version;

    @Value("${api.docs.contact.name}")
    private String contactName;

    @Value("${api.docs.contact.email}")
    private String contactEmail;

    @Value("${api.docs.contact.url}")
    private String contactUrl;

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
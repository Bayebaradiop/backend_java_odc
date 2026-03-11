package com.medibook.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI medibookOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MEDIBOOK API")
                        .version("1.0.0")
                        .description("""
                                ## API de gestion de cabinets médicaux SaaS
                                
                                ### Architecture
                                - **Super Admin** : Gestion de la plateforme et des cabinets
                                - **Admin** : Gestion d'un cabinet médical
                                - **Médecin** : Gestion des plannings et rendez-vous
                                - **Secrétaire** : Gestion des rendez-vous
                                - **Patient** : Consultation et prise de rendez-vous
                                
                                ### Authentification
                                L'API utilise l'authentification JWT. Connectez-vous via `/api/auth/login` pour obtenir un token.
                                """)
                        .contact(new Contact()
                                .name("Équipe Medibook")
                                .email("contact@medibook.com"))
                        .license(new License()
                                .name("Licence Propriétaire")
                                .url("https://medibook.com/licence")))
                .tags(List.of(
                        new Tag()
                                .name("Auth")
                                .description("API d'authentification (login, logout, profil)"),
                        new Tag()
                                .name("Cabinets")
                                .description("API de gestion des cabinets (Super Admin)"),
                        new Tag()
                                .name("Specialites")
                                .description("API de gestion des spécialités médicales (Admin)"),
                        new Tag()
                                .name("Users")
                                .description("API de gestion des utilisateurs (Admin, Super Admin)")
                ))
                .addSecurityItem(new SecurityRequirement().addList("JWT"))
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT token obtenu via /api/auth/login")));
    }
}

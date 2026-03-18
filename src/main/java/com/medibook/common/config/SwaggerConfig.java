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
                        // === Auth ===
                        new Tag()
                                .name("Authentication")
                                .description("API d'authentification (login, register, logout, profil) — /api/auth"),
                        new Tag()
                                .name("Mot de passe oublié")
                                .description("API de réinitialisation du mot de passe — /api/auth"),
                        // === Super Admin ===
                        new Tag()
                                .name("Cabinets")
                                .description("API de gestion des cabinets médicaux (Super Admin) — /api/super-admin/cabinets"),
                        new Tag()
                                .name("Dashboard - Super Admin")
                                .description("Tableau de bord Super Admin — /api/super-admin/dashboard"),
                        new Tag()
                                .name("Statistiques - Super Admin")
                                .description("Statistiques globales de la plateforme — /api/super-admin/stats"),
                        // === Admin ===
                        new Tag()
                                .name("Specialites")
                                .description("API de gestion des spécialités médicales (Admin) — /api/admin/specialites"),
                        new Tag()
                                .name("Users")
                                .description("Gestion des médecins et secrétaires (Admin) — /api/admin"),
                        new Tag()
                                .name("Statistiques - Admin")
                                .description("Statistiques du cabinet (Admin) — /api/admin/stats"),
                        // === Secrétaire ===
                        new Tag()
                                .name("Secrétaire")
                                .description("API de consultation des médecins du cabinet — /api/secretaire"),
                        new Tag()
                                .name("Planning - Secrétaire")
                                .description("Création de plannings hebdomadaires — /api/secretaire/planning"),
                        new Tag()
                                .name("Créneaux - Secrétaire")
                                .description("Consultation et suppression des créneaux (auto-générés) — /api/secretaire/creneaux"),
                        new Tag()
                                .name("Rendez-vous - Secrétaire")
                                .description("Gestion des RDV du cabinet — /api/secretaire/rdv"),
                        // === Médecin ===
                        new Tag()
                                .name("Planning - Médecin")
                                .description("Consultation de mes plannings — /api/medecin/plannings"),
                        new Tag()
                                .name("Rendez-vous - Médecin")
                                .description("Gestion de mes RDV — /api/medecin/rdv"),
                        new Tag()
                                .name("Statistiques - Médecin")
                                .description("Mes statistiques — /api/medecin/stats"),
                        // === Patient ===
                        new Tag()
                                .name("Recherche - Patient")
                                .description("Recherche de cabinets, spécialités et médecins — /api/patient"),
                        new Tag()
                                .name("Disponibilités - Patient")
                                .description("Consultation des créneaux disponibles — /api/patient/medecins/{id}/disponibilites"),
                        new Tag()
                                .name("Rendez-vous - Patient")
                                .description("Gestion de mes rendez-vous — /api/patient/rdv")
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

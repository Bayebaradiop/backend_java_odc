package com.medibook.common.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Service centralisé pour la gestion de la sécurité
 * Utilisé par tous les services pour récupérer l'utilisateur connecté
 */
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    private static final String USER_NOT_FOUND = "Utilisateur non trouvé";

    /**
     * Récupère l'utilisateur connecté depuis le contexte de sécurité
     */
    public Utilisateur getUtilisateurConnecte() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmailWithCabinetAndSpecialite(email)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND));
    }

    /**
     * Récupère l'email de l'utilisateur connecté
     */
    public String getEmailConnecte() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * Vérifie si un utilisateur est connecté
     */
    public boolean isAuthenticated() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
    }
}

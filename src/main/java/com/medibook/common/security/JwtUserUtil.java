package com.medibook.common.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.medibook.user.repository.UserRepository;

/**
 * Utilitaire pour extraire les informations de l'utilisateur connecté
 */
@Component
@RequiredArgsConstructor
public class JwtUserUtil {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    /**
     * Extrait l'ID de l'utilisateur depuis le token JWT
     */
    public Long getCurrentUserId() {
        Long userIdFromContext = extractUserIdFromSecurityContext();
        if (userIdFromContext != null) {
            return userIdFromContext;
        }

        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }

        String token = extractTokenFromRequest(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return null;
        }

        return jwtTokenProvider.getUserIdFromToken(token);
    }

    /**
     * Extrait l'email de l'utilisateur depuis le token JWT
     */
    public String getCurrentUserEmail() {
        String emailFromContext = extractEmailFromSecurityContext();
        if (emailFromContext != null) {
            return emailFromContext;
        }

        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }

        String token = extractTokenFromRequest(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return null;
        }

        return jwtTokenProvider.getEmailFromToken(token);
    }

    /**
     * Extrait le rôle de l'utilisateur depuis le token JWT
     */
    public String getCurrentUserRole() {
        String roleFromContext = extractRoleFromSecurityContext();
        if (roleFromContext != null) {
            return roleFromContext;
        }

        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }

        String token = extractTokenFromRequest(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            return null;
        }

        return jwtTokenProvider.getRoleFromToken(token);
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        // Extract from cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        // Extract from Authorization header
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    private Long extractUserIdFromSecurityContext() {
        String email = extractEmailFromSecurityContext();
        if (email == null) {
            return null;
        }

        return userRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElse(null);
    }

    private String extractEmailFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String email = authentication.getName();
        if (email == null || email.isBlank() || "anonymousUser".equals(email)) {
            return null;
        }

        return email;
    }

    private String extractRoleFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring("ROLE_".length()))
                .findFirst()
                .orElse(null);
    }
}

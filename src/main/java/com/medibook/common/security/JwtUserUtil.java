package com.medibook.common.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Utilitaire pour extraire les informations de l'utilisateur connecté
 */
@Component
@RequiredArgsConstructor
public class JwtUserUtil {

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Extrait l'ID de l'utilisateur depuis le token JWT
     */
    public Long getCurrentUserId() {
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
}

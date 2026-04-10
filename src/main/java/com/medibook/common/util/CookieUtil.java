package com.medibook.common.util;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

    private static final String COOKIE_NAME = "access_token";

    // Crée un cookie HTTP-Only contenant le token
    public ResponseCookie createTokenCookie(String token, long maxAgeSeconds) {
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)  
                .secure(true)         
                .sameSite("None")  
                .path("/")             
                .maxAge(maxAgeSeconds) 
                .build();
    }

    // Crée un cookie vide qui expire immédiatement (pour le logout)
    public ResponseCookie deleteTokenCookie() {
        return ResponseCookie.from(COOKIE_NAME, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)         
                .build();
    }

    // Retourne le nom du cookie (utilisé dans le filtre JWT)
    public String getCookieName() {
        return COOKIE_NAME;
    }
}

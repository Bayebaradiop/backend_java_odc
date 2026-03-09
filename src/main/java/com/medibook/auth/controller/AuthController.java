package com.medibook.auth.controller;

import com.medibook.auth.dto.*;
import com.medibook.auth.message.MessageSucces;
import com.medibook.auth.service.AuthService;
import com.medibook.common.security.JwtTokenProvider;
import com.medibook.common.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final JwtTokenProvider jwtTokenProvider;

    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        AuthService.AuthResult result = authService.login(request);

        ResponseCookie cookie = cookieUtil.createTokenCookie(
                result.token(), jwtTokenProvider.getExpirationInSeconds());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of(
                        "message", MessageSucces.CONNEXION_REUSSIE,
                        "user", result.response()
                ));
    }
    


    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        AuthService.AuthResult result = authService.register(request);

        ResponseCookie cookie = cookieUtil.createTokenCookie(
                result.token(), jwtTokenProvider.getExpirationInSeconds());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of(
                        "message", MessageSucces.INSCRIPTION_REUSSIE,
                        "user", result.response()
                ));
    }


    // POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = cookieUtil.deleteTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", MessageSucces.DECONNEXION_REUSSIE));
    }
}

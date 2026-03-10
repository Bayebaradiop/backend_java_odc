package com.medibook.auth.controller;

import com.medibook.auth.dto.*;
import com.medibook.auth.message.MessageSucces;
import com.medibook.auth.service.AuthService;
import com.medibook.common.security.JwtTokenProvider;
import com.medibook.common.util.CookieUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API d'authentification - Connexion, inscription, déconnexion")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "Connexion", description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect"),
        @ApiResponse(responseCode = "403", description = "Compte inactif")
    })
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


    @Operation(summary = "Inscription", description = "Crée un nouveau compte patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inscription réussie",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "400", description = "Email ou téléphone déjà utilisé")
    })
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


    @Operation(summary = "Déconnexion", description = "Déconnecte l'utilisateur en supprimant le cookie JWT")
    @ApiResponse(responseCode = "200", description = "Déconnexion réussie")
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = cookieUtil.deleteTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("message", MessageSucces.DECONNEXION_REUSSIE));
    }
}

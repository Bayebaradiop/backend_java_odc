package com.medibook.auth.controller;

import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.medibook.auth.dto.LoginRequest;
import com.medibook.auth.dto.RegisterRequest;
import com.medibook.auth.message.MessageSucces;
import com.medibook.auth.service.AuthService;
import com.medibook.common.security.JwtTokenProvider;
import com.medibook.common.util.CookieUtil;
import com.medibook.user.dto.UserRequest;
import com.medibook.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API d'authentification - Connexion, inscription, déconnexion")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Operation(summary = "Connexion", description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Connexion réussie"),
        @ApiResponse(responseCode = "401", description = "Email ou mot de passe incorrect"),
        @ApiResponse(responseCode = "403", description = "Compte inactif")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthService.AuthResult result = authService.login(request);

        ResponseCookie cookie = cookieUtil.createTokenCookie(
                result.token(), jwtTokenProvider.getExpirationInSeconds());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of(
                        "message", MessageSucces.CONNEXION_REUSSIE,
                        "user", result.response(),
                        "token", result.token()
                ));
    }

    @Operation(summary = "Inscription", description = "Crée un nouveau compte patient")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inscription réussie"),
        @ApiResponse(responseCode = "400", description = "Email ou téléphone déjà utilisé")
    })
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        AuthService.AuthResult result = authService.register(request);

        ResponseCookie cookie = cookieUtil.createTokenCookie(
                result.token(), jwtTokenProvider.getExpirationInSeconds());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of(
                        "message", MessageSucces.INSCRIPTION_REUSSIE,
                        "user", result.response(),
                        "token", result.token()
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

    @Operation(summary = "Mon profil", description = "Retourne les informations du profil connecté")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }

    @Operation(summary = "Modifier mon profil", description = "Met à jour les informations du profil connecté")
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }

    @Operation(summary = "Modifier ma photo de profil", description = "Upload une nouvelle photo de profil")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Photo mise à jour"),
        @ApiResponse(responseCode = "400", description = "Fichier invalide")
    })
    @PutMapping("/profile/photo")
    public ResponseEntity<?> updateProfilePhoto(@RequestParam("photo") MultipartFile photo) {
        return ResponseEntity.ok(userService.updateProfilePhoto(photo));
    }
    
}

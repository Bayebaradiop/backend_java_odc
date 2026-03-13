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
import org.springframework.web.bind.annotation.RestController;

import com.medibook.auth.dto.LoginRequest;
import com.medibook.auth.dto.RegisterRequest;
import com.medibook.auth.message.MessageSucces;
import com.medibook.auth.service.AuthService;
import com.medibook.common.security.JwtTokenProvider;
import com.medibook.common.util.CookieUtil;
import com.medibook.user.dto.UserRequest;
import com.medibook.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

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
                        "user", result.response(),
                        "token", result.token()
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
                        "user", result.response(),
                        "token", result.token()
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


    // GET /api/auth/profile
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        return ResponseEntity.ok(userService.getProfile());
    }


    // PUT /api/auth/profile
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody UserRequest request) {
        return ResponseEntity.ok(userService.updateProfile(request));
    }
    
}

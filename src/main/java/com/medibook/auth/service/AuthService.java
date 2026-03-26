package com.medibook.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.medibook.auth.dto.AuthResponse;
import com.medibook.auth.dto.LoginRequest;
import com.medibook.auth.dto.RegisterRequest;
import com.medibook.auth.message.MessageErreur;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;
import com.medibook.common.security.JwtTokenProvider;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // --- LOGIN ---
    public AuthResult login(LoginRequest request) {
        // 1. Chercher l'utilisateur par email
        Utilisateur user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException(MessageErreur.EMAIL_NON_TROUVE));

        // 2. Vérifier le mot de passe
        if (!passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            throw new RuntimeException(MessageErreur.MDP_INCORRECT);
        }

        // 3. Vérifier que le compte est actif
        if (user.getStatus() != Status.ACTIF) {
            throw new RuntimeException(MessageErreur.COMPTE_INACTIF);
        }

        // 4. Générer le token + construire la réponse
        return buildAuthResult(user);
    }
    

    // --- REGISTER (patient uniquement) ---
    public AuthResult register(RegisterRequest request) {
        // 1. Vérifier unicité email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException(MessageErreur.EMAIL_DEJA_UTILISE);
        }

        // 2. Vérifier unicité téléphone
        if (userRepository.existsByTelephone(request.getTelephone())) {
            throw new RuntimeException(MessageErreur.TELEPHONE_DEJA_UTILISE);
        }

        // 3. Créer l'utilisateur
        Utilisateur user = Utilisateur.builder()
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(Role.PATIENT)
                .status(Status.ACTIF)
                .build();

        userRepository.save(user);

        // 4. Générer le token + construire la réponse
        return buildAuthResult(user);
    }
    

    // --- Méthode commune : token + réponse ---
    private AuthResult buildAuthResult(Utilisateur user) {
        String token = jwtTokenProvider.generateToken(
                user.getId(), user.getEmail(), user.getRole().name());

        AuthResponse response = AuthResponse.builder()
                .id(user.getId())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();

        return new AuthResult(token, response);
    }

    // Record interne pour retourner token + response ensemble
    public record AuthResult(String token, AuthResponse response) {}
}

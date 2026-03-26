package com.medibook.auth.service;

import com.medibook.auth.message.MessageErreur;
import com.medibook.common.service.BrevoMailService;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final UserRepository userRepository;
    private final BrevoMailService brevoMailService;
    private final PasswordEncoder passwordEncoder;

    private record ResetCode(String code, Instant expiration) {}
    private final Map<String, ResetCode> resetCodes = new ConcurrentHashMap<>();

    private static final int CODE_LENGTH = 6;
    private static final int CODE_EXPIRATION_MINUTES = 15;

    public void sendResetCode(String email) {
        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(MessageErreur.EMAIL_NON_TROUVE));

        String code = generateCode();
        resetCodes.put(email, new ResetCode(code, Instant.now().plusSeconds(CODE_EXPIRATION_MINUTES * 60L)));

        try {
            String textContent = "Bonjour " + user.getPrenom() + ",\n\n"
                    + "Votre code de réinitialisation est : " + code + "\n\n"
                    + "Ce code expire dans " + CODE_EXPIRATION_MINUTES + " minutes.\n\n"
                    + "L'équipe MediBook";
            brevoMailService.sendEmail(email, "MediBook - Code de réinitialisation", textContent);
            log.info("Code de réinitialisation envoyé à {}", email);
        } catch (Exception e) {
            log.error("Échec de l'envoi du code à {}: {}", email, e.getMessage());
            throw new RuntimeException(MessageErreur.ERREUR_ENVOI_EMAIL);
        }
    }

    @Transactional
    public void resetPassword(String email, String code, String newPassword) {
        ResetCode resetCode = resetCodes.get(email);

        if (resetCode == null || !resetCode.code().equals(code)) {
            throw new RuntimeException(MessageErreur.CODE_INVALIDE);
        }

        if (Instant.now().isAfter(resetCode.expiration())) {
            resetCodes.remove(email);
            throw new RuntimeException(MessageErreur.CODE_EXPIRE);
        }

        Utilisateur user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(MessageErreur.EMAIL_NON_TROUVE));

        user.setMotDePasse(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        resetCodes.remove(email);

        log.info("Mot de passe réinitialisé pour {}", email);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}

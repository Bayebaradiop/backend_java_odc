package com.medibook.auth.controller;

import com.medibook.auth.dto.ForgotPasswordRequest;
import com.medibook.auth.dto.ResetPasswordRequest;
import com.medibook.auth.message.MessageSucces;
import com.medibook.auth.service.PasswordResetService;
import com.medibook.common.dto.ApiStandardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Mot de passe oublié", description = "API de réinitialisation du mot de passe")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    @Operation(summary = "Demander un code de réinitialisation", description = "Envoie un code à 6 chiffres par email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.CODE_ENVOYE),
            @ApiResponse(responseCode = "404", description = "Email non trouvé")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiStandardResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendResetCode(request.getEmail());
        return ResponseEntity.ok(ApiStandardResponse.success(null, MessageSucces.CODE_ENVOYE));
    }

    @Operation(summary = "Réinitialiser le mot de passe", description = "Vérifie le code et change le mot de passe")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.MOT_DE_PASSE_REINITIALISE),
            @ApiResponse(responseCode = "400", description = "Code invalide ou expiré")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ApiStandardResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getEmail(), request.getCode(), request.getNewPassword());
        return ResponseEntity.ok(ApiStandardResponse.success(null, MessageSucces.MOT_DE_PASSE_REINITIALISE));
    }
}

package com.medibook.user.controller;

import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.security.JwtUserUtil;
import com.medibook.user.dto.UserResponse;
import com.medibook.user.service.MedecinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Contrôleur pour les secrétaires
 */
@RestController
@RequestMapping("/api/secretaire")
@RequiredArgsConstructor
@Tag(name = "Secrétaire", description = "API pour les secrétaires")
@Slf4j
public class SecretaireController {

    private final MedecinService medecinService;
    private final JwtUserUtil jwtUserUtil;

    @Operation(
            summary = "Liste des médecins par spécialité",
            description = "Retourne les médecins du cabinet ayant la même spécialité que le/la secrétaire"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médecins récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit - Réservé au secretary")
    })
    @GetMapping("/medecins")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<List<UserResponse>>> getMedecinsBySpecialite() {
        Long userId = jwtUserUtil.getCurrentUserId();
        List<UserResponse> medecins = medecinService.getMedecinsBySpecialite(userId);
        return ResponseEntity.ok(ApiStandardResponse.success(medecins, "Médecins récupérés avec succès"));
    }
}


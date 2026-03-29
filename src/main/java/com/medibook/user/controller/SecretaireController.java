package com.medibook.user.controller;

import com.medibook.cabinet.dto.CabinetResponseDTO;
import com.medibook.cabinet.mapper.CabinetMapper;
import com.medibook.cabinet.entity.Cabinet;
import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.security.JwtUserUtil;
import com.medibook.user.dto.UserResponse;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final CabinetMapper cabinetMapper;

    // ==================== MON CABINET ====================

    @Operation(summary = "Mon cabinet", description = "Retourne les informations du cabinet du secrétaire connecté")
    @GetMapping("/mon-cabinet")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<?> getMonCabinet() {
        Long userId = jwtUserUtil.getCurrentUserId();
        Utilisateur secretaire = userRepository.findByIdWithCabinetAndSpecialite(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Cabinet cabinet = secretaire.getCabinet();
        if (cabinet == null) {
            return ResponseEntity.notFound().build();
        }
        CabinetResponseDTO dto = cabinetMapper.toResponseDTO(cabinet);
        return ResponseEntity.ok(dto);
    }

    // ==================== MÉDECINS ====================

    @Operation(
            summary = "Liste des médecins du cabinet",
            description = "Retourne tous les médecins du cabinet du/de la secrétaire connecté(e)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Médecins récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès réservé aux secrétaires")
    })
    @GetMapping("/medecins")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<List<UserResponse>>> getMedecinsDuCabinet() {
        Long userId = jwtUserUtil.getCurrentUserId();
        List<UserResponse> medecins = medecinService.getMedecinsDuCabinet(userId);
        return ResponseEntity.ok(ApiStandardResponse.success(medecins, "Médecins récupérés avec succès"));
    }
}

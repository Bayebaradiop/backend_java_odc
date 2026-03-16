package com.medibook.planning.controller;

import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.security.JwtUserUtil;
import com.medibook.planning.dto.PlanningRequest;
import com.medibook.planning.dto.PlanningResponse;
import com.medibook.planning.message.MessageSucces;
import com.medibook.planning.service.PlanningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur pour la gestion des plannings par les secrétaires
 */
@RestController
@RequestMapping("/api/secretaire/planning")
@RequiredArgsConstructor
@Tag(name = "Planning - Secrétaire", description = "API de gestion des plannings pour les secrétaires")
@Slf4j
public class SecretairePlanningController {

    private final PlanningService planningService;
    private final JwtUserUtil jwtUserUtil;

    @Operation(
            summary = "Créer un template de planning",
            description = "Crée un template de planning hebdomadaire pour un médecin. "
                    + "Le médecin doit avoir la même spécialité que le/la secrétaire."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Planning créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit - Réservé au secretary de la même spécialité")
    })
    @PostMapping
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<?> createPlanning(
            @Parameter(description = "Données du planning")
            @Valid @RequestBody PlanningRequest request) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        PlanningResponse response = planningService.createPlanning(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiStandardResponse.success(response, MessageSucces.PLANNING_CREE));
    }
}

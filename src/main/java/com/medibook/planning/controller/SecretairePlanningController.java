package com.medibook.planning.controller;

import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.security.JwtUserUtil;
import com.medibook.planning.dto.PlanningRequest;
import com.medibook.planning.dto.PlanningResponse;
import com.medibook.planning.message.MessageErreur;
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

import java.util.List;

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

    @Operation(summary = "Plannings d'un médecin", description = "Liste les plannings hebdomadaires d'un médecin de la même spécialité")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.PLANNINGS_RECUPERES),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_SECRETAIRE_SPECIALITE)
    })
    @GetMapping("/medecin/{medecinId}")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<List<PlanningResponse>>> getPlanningsMedecin(
            @Parameter(description = "ID du médecin") @PathVariable Long medecinId) {
        List<PlanningResponse> plannings = planningService.getPlanningsByMedecin(medecinId);
        return ResponseEntity.ok(ApiStandardResponse.success(plannings, MessageSucces.PLANNINGS_RECUPERES));
    }

    @Operation(
            summary = "Créer un template de planning",
            description = "Crée un template de planning hebdomadaire pour un médecin. "
                    + "Le médecin doit avoir la même spécialité que le/la secrétaire."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = MessageSucces.PLANNING_CREE),
            @ApiResponse(responseCode = "400", description = MessageErreur.HEURE_INVALIDE),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_SECRETAIRE_SPECIALITE)
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

    @Operation(summary = "Supprimer un planning", description = "Supprime un template de planning hebdomadaire")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.PLANNING_SUPPRIME),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "404", description = MessageErreur.PLANNING_NON_TROUVE)
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<Void>> deletePlanning(
            @Parameter(description = "ID du planning") @PathVariable Long id) {
        planningService.deletePlanning(id);
        return ResponseEntity.ok(ApiStandardResponse.success(null, MessageSucces.PLANNING_SUPPRIME));
    }
}

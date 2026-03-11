package com.medibook.specialite.controller;

import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.security.JwtUserUtil;
import com.medibook.specialite.dto.SpecialiteRequest;
import com.medibook.specialite.dto.SpecialiteResponse;
import com.medibook.specialite.service.SpecialiteService;
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
 * Contrôleur pour la gestion des spécialités
 */
@RestController
@RequestMapping("/api/admin/specialites")
@RequiredArgsConstructor
@Tag(name = "Specialites", description = "API de gestion des spécialités médicales")
@Slf4j
public class SpecialiteController {

    private final SpecialiteService specialiteService;
    private final JwtUserUtil jwtUserUtil;

    @Operation(
            summary = "Créer une spéciale",
            description = "Crée une nouvelle spécialité pour le cabinet de l'admin. Réservé à l'ADMIN du cabinet."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Spécialité créée avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit - Réservé à l'ADMIN du cabinet")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSpecialite(
            @Valid @RequestBody SpecialiteRequest request) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        SpecialiteResponse response = specialiteService.createSpecialite(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiStandardResponse.success(response, "Spécialité créée avec succès"));
    }

    @Operation(summary = "Liste des spécialités", description = "Retourne toutes les spécialités du cabinet de l'admin")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SpecialiteResponse>> getSpecialites() {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        return ResponseEntity.ok(specialiteService.getSpecialitesByAdmin(userId));
    }

    @Operation(summary = "Récupérer une spécialités par ID", description = "Retourne une spécialité spécifique")
    @GetMapping("/{specialiteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SpecialiteResponse> getSpecialiteById(
            @Parameter(description = "ID de la spécialités", required = true)
            @PathVariable Long specialiteId) {
        
        return ResponseEntity.ok(specialiteService.getSpecialiteById(specialiteId));
    }

    @Operation(
            summary = "Mettre à jour une spécialité",
            description = "Met à jour une spécialité existante. Réservé à l'ADMIN du cabinet."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spécialité mise à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    @PutMapping("/{specialiteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSpecialite(
            @Parameter(description = "ID de la spécialités", required = true)
            @PathVariable Long specialiteId,
            
            @Valid @RequestBody SpecialiteRequest request) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiStandardResponse.error("Non authentifié", "ERR_UNAUTHORIZED"));
        }
        
        SpecialiteResponse response = specialiteService.updateSpecialite(specialiteId, request, userId);
        return ResponseEntity.ok(ApiStandardResponse.success(response, "Spécialité mise à jour avec succès"));
    }

    @Operation(
            summary = "Supprimer une spécialités",
            description = "Supprime une spécialité. Réservé à l'ADMIN du cabinet."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Spécialité supprimée avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    @DeleteMapping("/{specialiteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSpecialite(
            @Parameter(description = "ID de la spécialités", required = true)
            @PathVariable Long specialiteId) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiStandardResponse.error("Non authentifié", "ERR_UNAUTHORIZED"));
        }
        
        specialiteService.deleteSpecialite(specialiteId, userId);
        return ResponseEntity.ok(ApiStandardResponse.success(null, "Spécialité supprimée avec succès"));
    }
}

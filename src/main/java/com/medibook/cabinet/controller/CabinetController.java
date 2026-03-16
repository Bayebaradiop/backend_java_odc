package com.medibook.cabinet.controller;

import com.medibook.cabinet.dto.CabinetCreateDTO;
import com.medibook.cabinet.dto.CabinetResponseDTO;
import com.medibook.cabinet.message.MessageSucces;
import com.medibook.cabinet.service.CabinetService;
import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.security.JwtUserUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour la gestion des cabinets
 */
@RestController
@RequestMapping("/api/admin/cabinets")
@RequiredArgsConstructor
@Tag(name = "Cabinets", description = "API de gestion des cabinets médicaux")
@Slf4j
public class CabinetController {

    private final CabinetService cabinetService;
    private final JwtUserUtil jwtUserUtil;


    @Operation(
            summary = "Créer un cabinet",
            description = "Crée un nouveau cabinet médical avec son administrateur. Réservé au Super Admin."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Cabinet créé avec succès",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = CabinetResponseDTO.class))
            ),
            @ApiResponse(responseCode = "400", description = "Données invalides ou manquantes"),
            @ApiResponse(responseCode = "401", description = "Non authentifié - Token JWT requis"),
            @ApiResponse(responseCode = "403", description = "Accès interdit - Réservé au Super Admin"),
            @ApiResponse(responseCode = "500", description = "Erreur serveur interne")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiStandardResponse<CabinetResponseDTO>> createCabinet(
            @Valid @ModelAttribute CabinetCreateDTO dto,
            @Parameter(description = "Logo du cabinet")
            @RequestPart(value = "logo", required = false) MultipartFile logo) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        log.info("createCabinet called with nom: {}, email: {}", dto.nom(), dto.email());
        
        CabinetResponseDTO response = cabinetService.createCabinet(dto, logo, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiStandardResponse.success(response, MessageSucces.CABINET_CREE));
    }

    @Operation(summary = "Récupérer tous les cabinets", description = "Retourne la liste de tous les cabinets")
    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<List<CabinetResponseDTO>> getAllCabinets() {
        return ResponseEntity.ok(cabinetService.getAllCabinets());
    }

    @Operation(summary = "Récupérer un cabinet par ID", description = "Retourne un cabinet spécifique")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN')")
    public ResponseEntity<CabinetResponseDTO> getCabinetById(@PathVariable Long id) {
        return ResponseEntity.ok(cabinetService.getCabinetById(id));
    }

    @Operation(summary = "Mettre à jour un cabinet", description = "Met à jour un cabinet existant. Réservé au Super Admin.")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiStandardResponse<CabinetResponseDTO>> updateCabinet(
            @PathVariable Long id,
            @Valid @ModelAttribute CabinetCreateDTO dto,
            @Parameter(description = "Logo du cabinet")
            @RequestPart(value = "logo", required = false) MultipartFile logo) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        CabinetResponseDTO response = cabinetService.updateCabinet(id, dto, logo, userId);
        return ResponseEntity.ok(ApiStandardResponse.success(response, MessageSucces.CABINET_MODIFIE));
    }

    @Operation(summary = "Supprimer un cabinet", description = "Supprime un cabinet. Réservé au Super Admin.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCabinet(@PathVariable Long id) {
        Long userId = jwtUserUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        cabinetService.deleteCabinet(id, userId);
        return ResponseEntity.ok(Map.of("message", MessageSucces.CABINET_SUPPRIME));
    }

    @Operation(summary = "Activer/Désactiver un cabinet", description = "Bascule le statut d'un cabinet. Réservé au Super Admin.")
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<CabinetResponseDTO> toggleCabinetStatus(@PathVariable Long id) {
        Long userId = jwtUserUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        CabinetResponseDTO response = cabinetService.toggleCabinetStatus(id, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mettre à jour le logo", description = "Met à jour le logo d'un cabinet. Réservé au Super Admin.")
    @PatchMapping("/{id}/logo")
    public ResponseEntity<CabinetResponseDTO> updateLogo(
            @PathVariable Long id,
            @RequestParam("logo") MultipartFile logo) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        return ResponseEntity.ok(cabinetService.updateLogo(id, logo, userId));
    }
}

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
            description = "Crée un nouveau cabinet médical. Réservé au Super Admin. Le logo peut être envoyé soit comme fichier (paramètre logo) soit comme URL externe (paramètre logoUrl).",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Données du cabinet à créer. Tous les champs marqués comme requis doivent être remplis.",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
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
    public ResponseEntity<?> createCabinet(
            @Parameter(description = "Nom du cabinet", required = true, example = "Cabinet Médical Dakar")
            @RequestParam("nom") String nom,
            
            @Parameter(description = "URL externe du logo (optionnel)", example = "https://exemple.com/logo.png")
            @RequestParam(value = "logoUrl", required = false) String logoUrl,
            
            @Parameter(description = "Couleur primaire au format hexadécimal", example = "#FF5733")
            @RequestParam(value = "couleurPrimaire", required = false) String couleurPrimaire,
            
            @Parameter(description = "Couleur secondaire au format hexadécimal", example = "#33FF57")
            @RequestParam(value = "couleurSecondaire", required = false) String couleurSecondaire,
            
            @Parameter(description = "Adresse complète du cabinet", required = true, example = "Dakar, Senegal")
            @RequestParam("adresse") String adresse,
            
            @Parameter(description = "Numéro de téléphone", required = true, example = "+221 33 123 45 67")
            @RequestParam("telephone") String telephone,
            
            @Parameter(description = "Adresse email du cabinet", required = true, example = "contact@cabinetdakar.com")
            @RequestParam("email") String email,
            
            @Parameter(description = "Fichier du logo (image)")
            @RequestParam(value = "logo", required = false) MultipartFile logo) {
        
        log.info("createCabinet called with nom: {}, email: {}", nom, email);
        
        Long userId = jwtUserUtil.getCurrentUserId();
        log.info("Current user ID: {}", userId);
        
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiStandardResponse.error("Non authentifié. Veuillez vous connecter.", "ERR_UNAUTHORIZED"));
        }
        
        // Créer le DTO manuellement
        CabinetCreateDTO dto = new CabinetCreateDTO(
                nom, logoUrl, couleurPrimaire, couleurSecondaire, adresse, telephone, email
        );
        
        CabinetResponseDTO response = cabinetService.createCabinet(dto, logo, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiStandardResponse.success(response, "Cabinet créé avec succès"));
    }

    @Operation(summary = "Récupérer tous les cabinets", description = "Retourne la liste de tous les cabinets")
    @GetMapping
    public ResponseEntity<List<CabinetResponseDTO>> getAllCabinets() {
        return ResponseEntity.ok(cabinetService.getAllCabinets());
    }

    @Operation(summary = "Récupérer un cabinet par ID", description = "Retourne un cabinet spécifique")
    @GetMapping("/{id}")
    public ResponseEntity<CabinetResponseDTO> getCabinetById(@PathVariable Long id) {
        return ResponseEntity.ok(cabinetService.getCabinetById(id));
    }

    @Operation(summary = "Mettre à jour un cabinet", description = "Met à jour un cabinet existant. Réservé au Super Admin.")
    @PutMapping("/{id}")
    public ResponseEntity<CabinetResponseDTO> updateCabinet(
            @PathVariable Long id,
            @RequestParam("nom") String nom,
            @RequestParam(value = "logoUrl", required = false) String logoUrl,
            @RequestParam(value = "couleurPrimaire", required = false) String couleurPrimaire,
            @RequestParam(value = "couleurSecondaire", required = false) String couleurSecondaire,
            @RequestParam("adresse") String adresse,
            @RequestParam("telephone") String telephone,
            @RequestParam("email") String email,
            @RequestParam(value = "logo", required = false) MultipartFile logo) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        CabinetCreateDTO dto = new CabinetCreateDTO(
                nom, logoUrl, couleurPrimaire, couleurSecondaire, adresse, telephone, email
        );
        
        return ResponseEntity.ok(cabinetService.updateCabinet(id, dto, logo, userId));
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

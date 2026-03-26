package com.medibook.user.controller;

import com.medibook.cabinet.dto.CabinetResponseDTO;
import com.medibook.cabinet.entity.Cabinet;
import com.medibook.cabinet.mapper.CabinetMapper;
import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.security.JwtUserUtil;
import com.medibook.user.dto.MedecinRequest;
import com.medibook.user.dto.SecretaireRequest;
import com.medibook.user.dto.UserResponse;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import com.medibook.user.message.MessageSucces;
import com.medibook.user.service.MedecinService;
import com.medibook.user.service.SecretaireService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Contrôleur pour la gestion des utilisateurs par l'ADMIN
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Users", description = "API de gestion des utilisateurs (médecins, secrétaires)")
@Slf4j
public class AdminController {

    private final MedecinService medecinService;
    private final SecretaireService secretaireService;
    private final JwtUserUtil jwtUserUtil;
    private final UserRepository userRepository;
    private final CabinetMapper cabinetMapper;

    // ==================== MON CABINET ====================

    @Operation(summary = "Mon cabinet", description = "Retourne les informations du cabinet de l'admin connecté")
    @GetMapping("/mon-cabinet")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getMonCabinet() {
        Long userId = jwtUserUtil.getCurrentUserId();
        Utilisateur admin = userRepository.findByIdWithCabinetAndSpecialite(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Cabinet cabinet = admin.getCabinet();
        if (cabinet == null) {
            return ResponseEntity.notFound().build();
        }
        CabinetResponseDTO dto = cabinetMapper.toResponseDTO(cabinet);
        return ResponseEntity.ok(dto);
    }

    // ==================== MÉDECINS ====================

    @Operation(
            summary = "Créer un médecin",
            description = "Crée un médecin avec photo uploadée vers Cloudinary (async). Un email de création de compte est envoyé automatiquement. Réservé à l'ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Médecin créé avec succès - email envoyé"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit")
    })
    @PostMapping(value = "/medecins", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createMedecin(
            @Parameter(description = "Données du médecin")
            @RequestPart("request") @Valid MedecinRequest request,
            
            @Parameter(description = "Fichier photo du médecin (optionnel)")
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        UserResponse response = medecinService.createMedecin(request, photo, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiStandardResponse.success(response, MessageSucces.MEDECIN_CREE));
    }

    @Operation(summary = "Liste des médecins", description = "Retourne tous les médecins du cabinet")
    @GetMapping("/medecins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getMedecins() {
        Long userId = jwtUserUtil.getCurrentUserId();
        return ResponseEntity.ok(medecinService.getMedecinsByAdmin(userId));
    }

    @Operation(summary = "Récupérer un médecin par ID")
    @GetMapping("/medecins/{medecinId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getMedecinById(
            @Parameter(description = "ID du médecin", required = true)
            @PathVariable Long medecinId) {
        Long userId = jwtUserUtil.getCurrentUserId();
        return ResponseEntity.ok(medecinService.getMedecinById(medecinId, userId));
    }

    @Operation(summary = "Mettre à jour un médecin")
    @PutMapping(value = "/medecins/{medecinId}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateMedecin(
            @Parameter(description = "ID du médecin", required = true)
            @PathVariable Long medecinId,
            
            @RequestPart("request") @Valid MedecinRequest request,
            
            @Parameter(description = "Fichier photo (laisser vide pour conserver l'actuelle)")
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        UserResponse response = medecinService.updateMedecin(medecinId, request, photo, userId);
        return ResponseEntity.ok(ApiStandardResponse.success(response, MessageSucces.MEDECIN_MODIFIE));
    }

    @Operation(summary = "Supprimer un médecin")
    @DeleteMapping("/medecins/{medecinId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteMedecin(
            @Parameter(description = "ID du médecin", required = true)
            @PathVariable Long medecinId) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        medecinService.deleteMedecin(medecinId, userId);
        return ResponseEntity.ok(ApiStandardResponse.success(null, MessageSucces.MEDECIN_SUPPRIME));
    }

    @Operation(summary = "Activer/Désactiver un médecin")
    @PatchMapping("/medecins/{medecinId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleMedecinStatus(
            @Parameter(description = "ID du médecin", required = true)
            @PathVariable Long medecinId) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        UserResponse response = medecinService.toggleMedecinStatus(medecinId, userId);
        return ResponseEntity.ok(ApiStandardResponse.success(response, MessageSucces.MEDECIN_STATUT_MODIFIE));
    }

    // ==================== SECRÉTAIRES ====================

    @Operation(
            summary = "Créer un/e secretary",
            description = "Crée un/e nouveau/nouvelle secretary pour le cabinet. Accepte soit un fichier photo, soit une URL photo. Réservé à l'ADMIN du cabinet."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Secrétaire créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit - Réservé à l'ADMIN")
    })
    @PostMapping(value = "/secretaires", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createSecretaire(
            @Parameter(description = "Données du/de la secretary")
            @RequestPart("request") @Valid SecretaireRequest request,
            
            @Parameter(description = "Fichier photo du/de la secretary")
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        UserResponse response = secretaireService.createSecretaire(request, photo, userId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiStandardResponse.success(response, MessageSucces.SECRETAIRE_CREE));
    }

    @Operation(summary = "Liste des secrétaires", description = "Retourne tous les secrétaires du cabinet")
    @GetMapping("/secretaires")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getSecretaires() {
        Long userId = jwtUserUtil.getCurrentUserId();
        return ResponseEntity.ok(secretaireService.getSecretairesByAdmin(userId));
    }

    @Operation(summary = "Récupérer un/e secretary par ID")
    @GetMapping("/secretaires/{secretaireId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getSecretaireById(
            @Parameter(description = "ID du/de la secretary", required = true)
            @PathVariable Long secretaireId) {
        Long userId = jwtUserUtil.getCurrentUserId();
        return ResponseEntity.ok(secretaireService.getSecretaireById(secretaireId, userId));
    }

    @Operation(summary = "Mettre à jour un/e secretary")
    @PutMapping(value = "/secretaires/{secretaireId}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateSecretaire(
            @Parameter(description = "ID du/de la secretary", required = true)
            @PathVariable Long secretaireId,
            
            @RequestPart("request") @Valid SecretaireRequest request,
            
            @Parameter(description = "Fichier photo (laisser vide pour conserver l'actuelle)")
            @RequestPart(value = "photo", required = false) MultipartFile photo) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        UserResponse response = secretaireService.updateSecretaire(secretaireId, request, photo, userId);
        return ResponseEntity.ok(ApiStandardResponse.success(response, MessageSucces.SECRETAIRE_MODIFIE));
    }

    @Operation(summary = "Supprimer un/e secretary")
    @DeleteMapping("/secretaires/{secretaireId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteSecretaire(
            @Parameter(description = "ID du/de la secretary", required = true)
            @PathVariable Long secretaireId) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        secretaireService.deleteSecretaire(secretaireId, userId);
        return ResponseEntity.ok(ApiStandardResponse.success(null, MessageSucces.SECRETAIRE_SUPPRIME));
    }

    @Operation(summary = "Activer/Désactiver un/e secretary")
    @PatchMapping("/secretaires/{secretaireId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleSecretaireStatus(
            @Parameter(description = "ID du/de la secretary", required = true)
            @PathVariable Long secretaireId) {
        
        Long userId = jwtUserUtil.getCurrentUserId();
        UserResponse response = secretaireService.toggleSecretaireStatus(secretaireId, userId);
        return ResponseEntity.ok(ApiStandardResponse.success(response, MessageSucces.SECRETAIRE_STATUT_MODIFIE));
    }
}

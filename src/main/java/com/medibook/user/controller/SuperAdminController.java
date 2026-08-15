package com.medibook.user.controller;

import com.medibook.cabinet.dto.CabinetResponseDTO;
import com.medibook.cabinet.service.CabinetService;
import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.dto.PaginatedResponse;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;
import com.medibook.common.exception.ResourceNotFoundException;
import com.medibook.user.dto.UserResponse;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.mapper.UserMapper;
import com.medibook.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/super-admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard - Super Admin", description = "API du tableau de bord Super Admin")
public class SuperAdminController {

    private final CabinetService cabinetService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Operation(summary = "Liste de tous les cabinets", description = "Retourne tous les cabinets de la plateforme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cabinets récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit - Réservé au Super Admin")
    })
    @GetMapping("/cabinets")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiStandardResponse<PaginatedResponse<CabinetResponseDTO>>> getAllCabinets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<CabinetResponseDTO> cabinets = PaginatedResponse.from(
                cabinetService.getAllCabinets(PageRequest.of(page, size)));
        return ResponseEntity.ok(ApiStandardResponse.successPaginated(cabinets, "Cabinets récupérés avec succès"));
    }

    @Operation(summary = "Liste de tous les admins", description = "Retourne tous les administrateurs de cabinets")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Admins récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit - Réservé au Super Admin")
    })
    @GetMapping("/admins")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiStandardResponse<PaginatedResponse<UserResponse>>> getAllAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        PaginatedResponse<UserResponse> admins = PaginatedResponse.from(
                userRepository.findByRole(Role.ADMIN, PageRequest.of(page, size))
                        .map(userMapper::toResponse));
        return ResponseEntity.ok(ApiStandardResponse.successPaginated(admins, "Admins récupérés avec succès"));
    }

    @Operation(summary = "Activer/Désactiver un admin", description = "Bascule le statut d'un administrateur de cabinet")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Statut modifié avec succès"),
            @ApiResponse(responseCode = "404", description = "Admin non trouvé")
    })
    @PatchMapping("/admins/{id}/toggle-status")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiStandardResponse<UserResponse>> toggleAdminStatus(@PathVariable Long id) {
        Utilisateur admin = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin non trouvé"));
        
        admin.setStatus(admin.getStatus() == Status.ACTIF ? Status.INACTIF : Status.ACTIF);
        Utilisateur saved = userRepository.save(admin);
        
        return ResponseEntity.ok(ApiStandardResponse.success(
                userMapper.toResponse(saved),
                "Statut de l'admin modifié avec succès"));
    }
}

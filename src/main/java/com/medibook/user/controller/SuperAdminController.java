package com.medibook.user.controller;

import com.medibook.cabinet.dto.CabinetResponseDTO;
import com.medibook.cabinet.service.CabinetService;
import com.medibook.common.dto.ApiStandardResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/super-admin/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard - Super Admin", description = "API du tableau de bord Super Admin")
public class SuperAdminController {

    private final CabinetService cabinetService;

    @Operation(summary = "Liste de tous les cabinets", description = "Retourne tous les cabinets de la plateforme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cabinets récupérés avec succès"),
            @ApiResponse(responseCode = "401", description = "Non authentifié"),
            @ApiResponse(responseCode = "403", description = "Accès interdit - Réservé au Super Admin")
    })
    @GetMapping("/cabinets")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiStandardResponse<List<CabinetResponseDTO>>> getAllCabinets() {
        List<CabinetResponseDTO> cabinets = cabinetService.getAllCabinets();
        return ResponseEntity.ok(ApiStandardResponse.success(cabinets, "Cabinets récupérés avec succès"));
    }
}

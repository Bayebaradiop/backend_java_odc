package com.medibook.stats.controller;

import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.stats.dto.StatsResponse;
import com.medibook.stats.message.MessageErreur;
import com.medibook.stats.message.MessageSucces;
import com.medibook.stats.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/super-admin/stats")
@RequiredArgsConstructor
@Tag(name = "Statistiques - Super Admin", description = "API de statistiques globales pour le Super Admin")
public class SuperAdminStatsController {

    private final StatsService statsService;

    @Operation(summary = "Statistiques globales", description = "Retourne les statistiques globales de la plateforme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.STATS_SUPER_ADMIN_RECUPEREES),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_SUPER_ADMIN_SEUL)
    })
    @GetMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiStandardResponse<StatsResponse>> getStatsGlobales() {
        StatsResponse stats = statsService.getSuperAdminStats();
        return ResponseEntity.ok(ApiStandardResponse.success(stats, MessageSucces.STATS_SUPER_ADMIN_RECUPEREES));
    }
}

package com.medibook.stats.controller;

import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.security.JwtUserUtil;
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
@RequestMapping("/api/medecin/stats")
@RequiredArgsConstructor
@Tag(name = "Statistiques - Médecin", description = "API de statistiques pour les médecins")
public class MedecinStatsController {

    private final StatsService statsService;
    private final JwtUserUtil jwtUserUtil;

    @Operation(summary = "Mes statistiques", description = "Retourne les statistiques du médecin connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.STATS_MEDECIN_RECUPEREES),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_MEDECIN_SEUL)
    })
    @GetMapping
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<ApiStandardResponse<StatsResponse>> getMesStats() {
        Long medecinId = jwtUserUtil.getCurrentUserId();
        StatsResponse stats = statsService.getMedecinStats(medecinId);
        return ResponseEntity.ok(ApiStandardResponse.success(stats, MessageSucces.STATS_MEDECIN_RECUPEREES));
    }
}

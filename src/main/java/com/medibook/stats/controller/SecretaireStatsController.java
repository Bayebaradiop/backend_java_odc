package com.medibook.stats.controller;

import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.security.SecurityService;
import com.medibook.stats.dto.StatsResponse;
import com.medibook.stats.message.MessageErreur;
import com.medibook.stats.message.MessageSucces;
import com.medibook.stats.service.StatsService;
import com.medibook.user.entity.Utilisateur;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/secretaire/stats")
@RequiredArgsConstructor
@Tag(name = "Statistiques - Secrétaire", description = "API de statistiques pour les secrétaires de cabinet")
public class SecretaireStatsController {

    private final StatsService statsService;
    private final SecurityService securityService;

    @Operation(summary = "Statistiques du cabinet", description = "Retourne les statistiques du cabinet de la secrétaire connectée")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.STATS_SECRETAIRE_RECUPEREES),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_SECRETAIRE_SEUL)
    })
    @GetMapping
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<ApiStandardResponse<StatsResponse>> getStatsCabinet() {
        Utilisateur secretaire = securityService.getUtilisateurConnecte();
        StatsResponse stats = statsService.getSecretaireStats(secretaire);
        return ResponseEntity.ok(ApiStandardResponse.success(stats, MessageSucces.STATS_SECRETAIRE_RECUPEREES));
    }
}

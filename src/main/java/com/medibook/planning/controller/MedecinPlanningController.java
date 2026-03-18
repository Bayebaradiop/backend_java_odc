package com.medibook.planning.controller;

import com.medibook.common.dto.ApiStandardResponse;
import com.medibook.common.security.JwtUserUtil;
import com.medibook.planning.dto.PlanningResponse;
import com.medibook.planning.message.MessageErreur;
import com.medibook.planning.message.MessageSucces;
import com.medibook.planning.service.PlanningService;
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
@RequestMapping("/api/medecin/plannings")
@RequiredArgsConstructor
@Tag(name = "Planning - Médecin", description = "API de consultation des plannings pour les médecins")
public class MedecinPlanningController {

    private final PlanningService planningService;
    private final JwtUserUtil jwtUserUtil;

    @Operation(summary = "Mes plannings", description = "Retourne les templates de planning hebdomadaire du médecin connecté")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = MessageSucces.PLANNINGS_RECUPERES),
            @ApiResponse(responseCode = "401", description = MessageErreur.NON_AUTHENTIFIE),
            @ApiResponse(responseCode = "403", description = MessageErreur.ACCES_MEDECIN_SEUL)
    })
    @GetMapping
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<ApiStandardResponse<List<PlanningResponse>>> getMesPlannings() {
        Long medecinId = jwtUserUtil.getCurrentUserId();
        List<PlanningResponse> plannings = planningService.getPlanningsByMedecin(medecinId);
        return ResponseEntity.ok(ApiStandardResponse.success(plannings, MessageSucces.PLANNINGS_RECUPERES));
    }
}

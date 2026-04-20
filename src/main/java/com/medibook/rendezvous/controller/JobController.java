package com.medibook.rendezvous.controller;

import com.medibook.rendezvous.service.RappelRendezVousService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/super-admin/jobs")
@RequiredArgsConstructor
public class JobController {

    private final RappelRendezVousService rappelService;

    @PostMapping("/rappel-rdv")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Map<String, String>> triggerRappel() {
        rappelService.envoyerRappels();
        return ResponseEntity.ok(Map.of("message", "Job de rappel exécuté avec succès"));
    }
}

package com.medibook.user.controller;

import com.medibook.cabinet.dto.CabinetResponseDTO;
import com.medibook.cabinet.entity.Cabinet;
import com.medibook.cabinet.mapper.CabinetMapper;
import com.medibook.common.security.JwtUserUtil;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Contrôleur pour les médecins
 */
@RestController
@RequestMapping("/api/medecin")
@RequiredArgsConstructor
@Tag(name = "Médecin", description = "API pour les médecins")
@Slf4j
public class MedecinController {

    private final JwtUserUtil jwtUserUtil;
    private final UserRepository userRepository;
    private final CabinetMapper cabinetMapper;

    @Operation(summary = "Mon cabinet", description = "Retourne les informations du cabinet du médecin connecté")
    @GetMapping("/mon-cabinet")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<?> getMonCabinet() {
        Long userId = jwtUserUtil.getCurrentUserId();
        Utilisateur medecin = userRepository.findByIdWithCabinetAndSpecialite(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Cabinet cabinet = medecin.getCabinet();
        if (cabinet == null) {
            return ResponseEntity.notFound().build();
        }
        CabinetResponseDTO dto = cabinetMapper.toResponseDTO(cabinet);
        return ResponseEntity.ok(dto);
    }
}

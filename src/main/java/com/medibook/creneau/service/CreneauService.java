package com.medibook.creneau.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;
import com.medibook.creneau.dto.CreneauRequest;
import com.medibook.creneau.dto.CreneauResponse;
import com.medibook.creneau.entity.Creneau;
import com.medibook.creneau.mapper.CreneauMapper;
import com.medibook.creneau.message.MessageErreur;
import com.medibook.creneau.repository.CreneauRepository;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CreneauService {

    private final CreneauRepository creneauRepository;
    private final UserRepository userRepository;
    private final CreneauMapper mapper;

    /**
     * Récupère les créneaux disponibles d'un médecin
     * - Si date fournie : créneaux de cette date
     * - Sinon : créneaux des 7 prochains jours
     */
    public List<CreneauResponse> getDisponibilitesMedecin(Long medecinId, LocalDate date) {
        userRepository.findById(medecinId)
                .filter(u -> u.getRole() == Role.MEDECIN && u.getStatus() == Status.ACTIF)
                .orElseThrow(() -> new EntityNotFoundException(MessageErreur.MEDECIN_NOT_FOUND));

        List<Creneau> creneaux;

        if (date != null) {
            creneaux = creneauRepository.findByMedecinIdAndDateAndDisponibleTrue(medecinId, date);
        } else {
            // Créneaux des 7 prochains jours
            LocalDate today = LocalDate.now();
            LocalDate endDate = today.plusDays(7);
            creneaux = creneauRepository.findByMedecinIdAndDateBetweenAndDisponibleTrueOrderByDateAscHeureDebutAsc(
                    medecinId, today, endDate);
        }

        return creneaux.stream()
                .map(mapper::toCreneauResponse)
                .toList();
    }

    
    
     // Récupère un créneau par son ID
    public CreneauResponse getCreneauById(Long id) {
        Creneau creneau = creneauRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageErreur.CRENEAU_NOT_FOUND));
        return mapper.toCreneauResponse(creneau);
    }

    // ===== Méthodes Secrétaire =====

    public List<CreneauResponse> getCreneauxMedecin(Long medecinId) {
        return creneauRepository.findByMedecinId(medecinId)
                .stream().map(mapper::toCreneauResponse).toList();
    }

    @Transactional
    public void supprimerCreneau(Long id) {
        Creneau creneau = creneauRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageErreur.CRENEAU_NOT_FOUND));
        if (!creneau.getDisponible()) {
            throw new IllegalStateException(MessageErreur.CRENEAU_NON_DISPONIBLE);
        }
        creneauRepository.delete(creneau);
    }
}

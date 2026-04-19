package com.medibook.ExceptionsPlanning.service;

import com.medibook.ExceptionsPlanning.dto.ExceptionRequest;
import com.medibook.ExceptionsPlanning.dto.ExceptionResponse;
import com.medibook.ExceptionsPlanning.entity.ExceptionsPlanning;
import com.medibook.ExceptionsPlanning.mapper.ExceptionMapper;
import com.medibook.ExceptionsPlanning.message.MessageErreur;
import com.medibook.ExceptionsPlanning.repository.ExceptionsPlanningRepository;
import com.medibook.common.enums.Role;
import com.medibook.common.exception.BusinessException;
import com.medibook.common.exception.ResourceNotFoundException;
import com.medibook.common.security.SecurityService;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

/**
 * Service pour la gestion des exceptions de planning (indisponibilités)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExceptionService {

    private final ExceptionsPlanningRepository exceptionRepository;
    private final UserRepository userRepository;
    private final ExceptionMapper exceptionMapper;
    private final SecurityService securityService;

    /**
     * Crée une exception de planning pour le médecin connecté
     */
    @Transactional
    public ExceptionResponse creerExceptionMedecin(ExceptionRequest request) {
        Utilisateur medecin = securityService.getUtilisateurConnecte();
        return sauvegarderException(medecin, request);
    }

    /**
     * Crée une exception de planning pour un médecin (par secrétaire)
     */
    @Transactional
    public ExceptionResponse creerExceptionPourMedecin(Long medecinId, ExceptionRequest request, Long secretaireId) {
        Utilisateur secretaire = validateAndGetSecretaire(secretaireId);
        Utilisateur medecin = validateAndGetMedecin(medecinId);
        validateBusinessRules(secretaire, medecin);
        return sauvegarderException(medecin, request);
    }

    /**
     * Récupère les exceptions du médecin connecté
     */
    @Transactional(readOnly = true)
    public List<ExceptionResponse> getMesExceptions() {
        Utilisateur medecin = securityService.getUtilisateurConnecte();
        return exceptionRepository.findByMedecinId(medecin.getId())
                .stream()
                .map(exceptionMapper::toResponse)
                .toList();
    }

    /**
     * Récupère les exceptions d'un médecin (par secrétaire/admin)
     */
    @Transactional(readOnly = true)
    public List<ExceptionResponse> getExceptionsByMedecin(Long medecinId, Long secretaireId) {
        Utilisateur secretaire = validateAndGetSecretaire(secretaireId);
        Utilisateur medecin = validateAndGetMedecin(medecinId);
        validateBusinessRules(secretaire, medecin);
        return exceptionRepository.findByMedecinId(medecinId)
                .stream()
                .map(exceptionMapper::toResponse)
                .toList();
    }

    /**
     * Supprime une exception de planning (médecin connecté)
     */
    @Transactional
    public void supprimerException(Long exceptionId) {
        Utilisateur medecin = securityService.getUtilisateurConnecte();
        ExceptionsPlanning exception = exceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageErreur.EXCEPTION_NON_TROUVEE));

        if (!exception.getMedecin().getId().equals(medecin.getId())) {
            throw new BusinessException(MessageErreur.ACCES_REFUSE);
        }

        exceptionRepository.delete(exception);
        log.info("Exception {} supprimée par le médecin {}", exceptionId, medecin.getEmail());
    }

    /**
     * Supprime une exception de planning (par secrétaire/admin)
     */
    @Transactional
    public void supprimerExceptionAdmin(Long exceptionId) {
        ExceptionsPlanning exception = exceptionRepository.findById(exceptionId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageErreur.EXCEPTION_NON_TROUVEE));
        exceptionRepository.delete(exception);
        log.info("Exception {} supprimée par admin", exceptionId);
    }

    // ========== Méthodes privées ==========

    private ExceptionResponse sauvegarderException(Utilisateur medecin, ExceptionRequest request) {
        LocalTime heureDebut = parseTime(request.heureDebut());
        LocalTime heureFin = parseTime(request.heureFin());

        validateHeures(heureDebut, heureFin);

        ExceptionsPlanning exception = ExceptionsPlanning.builder()
                .medecin(medecin)
                .dateDebut(request.date())
                .dateFin(request.date())
                .type(request.type())
                .heureDebut(heureDebut)
                .heureFin(heureFin)
                .motif(request.motif())
                .build();

        ExceptionsPlanning saved = exceptionRepository.save(exception);
        log.info("Exception de planning créée pour le médecin {} le {}", medecin.getEmail(), request.date());
        return exceptionMapper.toResponse(saved);
    }

    private LocalTime parseTime(String time) {
        if (time == null || time.isBlank()) return null;
        return LocalTime.parse(time);
    }

    private void validateHeures(LocalTime heureDebut, LocalTime heureFin) {
        if (heureDebut != null && heureFin != null && !heureFin.isAfter(heureDebut)) {
            throw new BusinessException(MessageErreur.HEURE_INVALIDE);
        }
    }

    private Utilisateur validateAndGetSecretaire(Long secretaireId) {
        Utilisateur secretaire = userRepository.findByIdWithCabinetAndSpecialite(secretaireId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageErreur.NON_AUTHENTIFIE));

        if (secretaire.getRole() != Role.SECRETAIRE) {
            throw new BusinessException(MessageErreur.ACCES_SECRETAIRE_SEUL);
        }

        return secretaire;
    }

    private Utilisateur validateAndGetMedecin(Long medecinId) {
        return userRepository.findByIdWithCabinetAndSpecialite(medecinId)
                .orElseThrow(() -> new ResourceNotFoundException(MessageErreur.MEDECIN_NON_TROUVE));
    }

    private void validateBusinessRules(Utilisateur secretaire, Utilisateur medecin) {
        if (medecin.getCabinet() == null || secretaire.getCabinet() == null
                || !medecin.getCabinet().getId().equals(secretaire.getCabinet().getId())) {
            throw new BusinessException(MessageErreur.MEDECIN_MEME_CABINET);
        }
    }
}

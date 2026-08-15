package com.medibook.specialite.service;

import com.medibook.common.enums.Role;
import com.medibook.common.exception.BusinessException;
import com.medibook.common.exception.FieldValidationException;
import com.medibook.common.exception.ResourceNotFoundException;
import com.medibook.specialite.dto.SpecialiteRequest;
import com.medibook.specialite.dto.SpecialiteResponse;
import com.medibook.specialite.entity.Specialite;
import com.medibook.specialite.mapper.SpecialiteMapper;
import com.medibook.specialite.repository.SpecialiteRepository;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service pour la gestion des spécialités
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SpecialiteService {

    private final SpecialiteRepository specialiteRepository;
    private final UserRepository userRepository;
    private final SpecialiteMapper specialiteMapper;

    /**
     * Crée une nouvelle spécialité pour le cabinet de l'admin
     * Seul l'ADMIN du cabinet peut créer une spécialité
     */
    @Transactional
    public SpecialiteResponse createSpecialite(SpecialiteRequest request, Long userId) {
        // 1. Valider que l'utilisateur est ADMIN et récupérer son cabinet
        Utilisateur admin = validateAdminAndGetCabinet(userId);
        
        // 2. Valider les données de la spécialité
        validateSpecialiteData(request, admin.getCabinet().getId());
        
        // 3. Créer la spécialité
        Specialite specialite = specialiteMapper.toEntity(request);
        specialite.setCabinet(admin.getCabinet());
        
        Specialite savedSpecialite = specialiteRepository.save(specialite);
        log.info("Spécialité créée: {} pour le cabinet {}", savedSpecialite.getNom(), admin.getCabinet().getNom());
        
        return specialiteMapper.toResponseDTO(savedSpecialite);
    }

    /**
     * Récupère toutes les spécialités du cabinet de l'admin
     */
    @Transactional(readOnly = true)
    public List<SpecialiteResponse> getSpecialitesByAdmin(Long userId) {
        Utilisateur admin = validateAdminAndGetCabinet(userId);
        return specialiteRepository.findByCabinetId(admin.getCabinet().getId()).stream()
                .map(specialiteMapper::toResponseDTO)
                .toList();
    }

    /**
     * Récupère une spécialité par son ID
     */
    @Transactional(readOnly = true)
    public SpecialiteResponse getSpecialiteById(Long id) {
        Specialite specialite = findSpecialiteById(id);
        return specialiteMapper.toResponseDTO(specialite);
    }

    /**
     * Met à jour une spécialité
     */
    @Transactional
    public SpecialiteResponse updateSpecialite(Long id, SpecialiteRequest request, Long userId) {
        // 1. Valider que l'utilisateur est ADMIN
        Utilisateur admin = validateAdminOfCabinet(userId, null);
        
        // 2. Récupérer la spécialité
        Specialite specialite = findSpecialiteById(id);
        
        // 3. Valider que l'admin est du même cabinet
        validateAdminOwnsSpecialite(admin, specialite);
        
        // 4. Valider les données pour la mise à jour
        validateSpecialiteUpdate(request, specialite);
        
        // 5. Mettre à jour
        specialiteMapper.updateFromRequest(request, specialite);
        
        Specialite savedSpecialite = specialiteRepository.save(specialite);
        log.info("Spécialité mise à jour: {}", savedSpecialite.getNom());
        
        return specialiteMapper.toResponseDTO(savedSpecialite);
    }

    /**
     * Supprime une spécialité
     */
    @Transactional
    public void deleteSpecialite(Long id, Long userId) {
        // 1. Valider que l'utilisateur est ADMIN
        Utilisateur admin = validateAdminOfCabinet(userId, null);
        
        // 2. Récupérer la spécialité
        Specialite specialite = findSpecialiteById(id);
        
        // 3. Valider que l'admin est du même cabinet
        validateAdminOwnsSpecialite(admin, specialite);
        
        specialiteRepository.delete(specialite);
        log.info("Spécialité supprimée: {}", specialite.getNom());
    }

    // ==================== Méthodes de validation ====================

    /**
     * Valide que l'utilisateur est ADMIN et retourne son cabinet
     */
    private Utilisateur validateAdminAndGetCabinet(Long userId) {
        Utilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(com.medibook.specialite.message.MessageErreur.UTILISATEUR_NON_TROUVE));

        if (user.getRole() != Role.ADMIN) {
            log.warn("L'utilisateur {} (rôle: {}) a tenté d'effectuer une action sans être ADMIN", 
                    user.getEmail(), user.getRole());
            throw new BusinessException(com.medibook.specialite.message.MessageErreur.ACCES_ADMIN_SEUL);
        }

        if (user.getCabinet() == null) {
            throw new BusinessException(com.medibook.specialite.message.MessageErreur.PAS_DE_CABINET);
        }

        return user;
    }

    /**
     * Valide que l'utilisateur est ADMIN du cabinet
     */
    private Utilisateur validateAdminOfCabinet(Long userId, Long cabinetId) {
        Utilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(com.medibook.specialite.message.MessageErreur.UTILISATEUR_NON_TROUVE));

        if (user.getRole() != Role.ADMIN) {
            log.warn("L'utilisateur {} (rôle: {}) a tenté d'effectuer une action sans être ADMIN", 
                    user.getEmail(), user.getRole());
            throw new BusinessException(com.medibook.specialite.message.MessageErreur.ACCES_ADMIN_SEUL);
        }

        // Vérifier que l'utilisateur est ADMIN du cabinet concerné
        if (cabinetId != null && (user.getCabinet() == null || !user.getCabinet().getId().equals(cabinetId))) {
            log.warn("L'utilisateur {} n'est pas ADMIN du cabinet {}", user.getEmail(), cabinetId);
            throw new BusinessException(com.medibook.specialite.message.MessageErreur.PAS_ADMIN_CABINET);
        }

        return user;
    }

    /**
     * Valide que l'admin possède la spécialité
     */
    private void validateAdminOwnsSpecialite(Utilisateur admin, Specialite specialite) {
        if (admin.getCabinet() == null || !admin.getCabinet().getId().equals(specialite.getCabinet().getId())) {
            throw new BusinessException(com.medibook.specialite.message.MessageErreur.PAS_ADMIN_CABINET);
        }
    }

    /**
     * Valide les données d'une nouvelle spécialité
     */
    private void validateSpecialiteData(SpecialiteRequest request, Long cabinetId) {
        if (specialiteRepository.existsByNomAndCabinetId(request.nom(), cabinetId)) {
            throw new FieldValidationException(
                    "Veuillez corriger les champs en erreur",
                    Map.of("nom", com.medibook.specialite.message.MessageErreur.SPECIALITE_DEJA_EXISTANTE)
            );
        }
    }

    /**
     * Valide les données lors de la mise à jour
     */
    private void validateSpecialiteUpdate(SpecialiteRequest request, Specialite currentSpecialite) {
        if (!currentSpecialite.getNom().equals(request.nom()) 
                && specialiteRepository.existsByNomAndCabinetId(request.nom(), currentSpecialite.getCabinet().getId())) {
            throw new FieldValidationException(
                    "Veuillez corriger les champs en erreur",
                    Map.of("nom", com.medibook.specialite.message.MessageErreur.SPECIALITE_DEJA_EXISTANTE)
            );
        }
    }



    /**
     * Trouve une spécialité par ID
     */
    private Specialite findSpecialiteById(Long id) {
        return specialiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(com.medibook.specialite.message.MessageErreur.SPECIALITE_NON_TROUVEE));
    }
}

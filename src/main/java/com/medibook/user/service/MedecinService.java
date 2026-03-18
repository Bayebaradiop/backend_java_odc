package com.medibook.user.service;

import com.medibook.cabinet.entity.Cabinet;
import com.medibook.cabinet.repository.CabinetRepository;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;
import com.medibook.common.event.MedecinCreatedEvent;
import com.medibook.common.exception.BusinessException;
import com.medibook.common.exception.ResourceNotFoundException;
import com.medibook.common.storage.MediaUploadService;
import com.medibook.specialite.entity.Specialite;
import com.medibook.specialite.repository.SpecialiteRepository;
import com.medibook.user.dto.MedecinRequest;
import com.medibook.user.dto.UserResponse;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.mapper.UserMapper;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service pour la gestion des médecins
 * Accepte soit un fichier photo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MedecinService {

    private final UserRepository userRepository;
    private final CabinetRepository cabinetRepository;
    private final SpecialiteRepository specialiteRepository;
    private final UserMapper userMapper;
    private final MediaUploadService mediaUploadService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Crée un médecin - accepte fichier photo uploadé vers Cloudinary
     */
    @Transactional
    public UserResponse createMedecin(MedecinRequest request, MultipartFile photoFile, Long adminId) {
        // 1. Valider que l'utilisateur est ADMIN et récupérer son cabinet
        Utilisateur admin = validateAdminAndGetCabinet(adminId);

        // 2. Valider les données du médecin
        validateMedecinData(request, admin.getCabinet().getId());

        // 3. Récupérer la spécialité
        Specialite specialite = findSpecialiteById(request.specialiteId());

        // 4. Valider que la spécialité appartient au cabinet
        validateSpecialiteInCabinet(specialite, admin.getCabinet());

        // 5. Créer le médecin
        Utilisateur medecin = userMapper.toEntityFromMedecin(request);
        medecin.setCabinet(admin.getCabinet());
        medecin.setSpecialite(specialite);
        medecin.setRole(Role.MEDECIN);
        medecin.setStatus(Status.ACTIF);
        medecin.setMotDePasse(passwordEncoder.encode(request.motDePasse()));

        // 6. Sauvegarder d'abord pour avoir l'ID
        Utilisateur savedMedecin = userRepository.save(medecin);
        log.info("Médecin créé: {} pour le cabinet {}", savedMedecin.getEmail(), admin.getCabinet().getNom());

        // 7. Gérer la photo: upload vers Cloudinary si fichier fourni
        handlePhotoCreation(savedMedecin, photoFile);

        // 8. Publier l'événement pour envoyer l'email de création de compte
        eventPublisher.publishEvent(new MedecinCreatedEvent(
                this,
                savedMedecin,
                request.motDePasse(),
                admin.getCabinet().getNom()
        ));

        return userMapper.toResponse(savedMedecin);
    }

    /**
     * Gère la photo lors de la création - upload vers Cloudinary
     */
    private void handlePhotoCreation(Utilisateur medecin, MultipartFile photoFile) {
        if (photoFile != null && !photoFile.isEmpty()) {
            String folder = "medibook/medecins/" + medecin.getId();
            String url = mediaUploadService.uploadImage(photoFile, folder);
            medecin.setPhoto(url);
            userRepository.save(medecin);
            log.info("Photo uploadée pour le médecin {}: {}", medecin.getEmail(), url);
        }
    }

    /**
     * Gère la photo lors de la mise à jour:
     * - Si nouveau fichier → upload + supprime l'ancienne
     * - Si rien de nouveau → conserve l'ancienne
     */
    private void handlePhoto(Utilisateur medecin, MultipartFile photoFile) {
        // Récupérer l'ancienne photo pour UPDATE
        String oldPhoto = medecin.getPhoto();
        
        if (photoFile != null && !photoFile.isEmpty()) {
            // Supprimer l'ancienne photo si elle existe
            if (oldPhoto != null && !oldPhoto.isEmpty()) {
                mediaUploadService.deleteImageAsync(oldPhoto);
            }
            // Upload le nouveau fichier de manière synchrone
            String folder = "medibook/medecins/" + medecin.getId();
            String url = mediaUploadService.uploadImage(photoFile, folder);
            medecin.setPhoto(url);
            log.info("Photo uploadée pour le médecin {}: {}", medecin.getEmail(), url);
        }
        // Si pas de nouveau fichier → conserver l'ancienne photo (pas de changement)
    }

    /**
     * Récupère tous les médecins du cabinet de l'admin
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getMedecinsByAdmin(Long adminId) {
        Utilisateur admin = validateAdminAndGetCabinet(adminId);
        return userRepository.findByRoleAndCabinetId(Role.MEDECIN, admin.getCabinet().getId()).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    /**
     * Récupère les médecins du cabinet par spécialité (pour secretary)
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getMedecinsBySpecialite(Long secretaireId) {
        Utilisateur secretaire = userRepository.findById(secretaireId)
                .orElseThrow(() -> new ResourceNotFoundException("Secrétaire non trouvé"));

        if (secretaire.getRole() != Role.SECRETAIRE) {
            throw new BusinessException("Accès réservé aux secrétaires");
        }

        if (secretaire.getCabinet() == null || secretaire.getSpecialite() == null) {
            throw new BusinessException("Le secrétaire doit appartenir à un cabinet et avoir une spécialité");
        }

        List<Utilisateur> medecins = userRepository.findByRoleAndCabinetIdAndSpecialiteId(
                Role.MEDECIN,
                secretaire.getCabinet().getId(),
                secretaire.getSpecialite().getId()
        );

        return medecins.stream().map(userMapper::toResponse).toList();
    }

    /**
     * Récupère un médecin par son ID
     */
    @Transactional(readOnly = true)
    public UserResponse getMedecinById(Long medecinId, Long adminId) {
        Utilisateur admin = validateAdminAndGetCabinet(adminId);
        Utilisateur medecin = findMedecinById(medecinId);
        validateMedecinInCabinet(medecin, admin.getCabinet());
        return userMapper.toResponse(medecin);
    }

    /**
     * Met à jour un médecin
     */
    @Transactional
    public UserResponse updateMedecin(Long medecinId, MedecinRequest request, MultipartFile photoFile, Long adminId) {
        Utilisateur admin = validateAdminAndGetCabinet(adminId);
        Utilisateur medecin = findMedecinById(medecinId);
        validateMedecinInCabinet(medecin, admin.getCabinet());
        validateMedecinUpdate(request, medecin, admin.getCabinet().getId());

        userMapper.updateFromMedecinRequest(request, medecin);

        if (request.specialiteId() != null) {
            Specialite specialite = findSpecialiteById(request.specialiteId());
            validateSpecialiteInCabinet(specialite, admin.getCabinet());
            medecin.setSpecialite(specialite);
        }

        if (request.motDePasse() != null && !request.motDePasse().isEmpty()) {
            medecin.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        }

        Utilisateur savedMedecin = userRepository.save(medecin);
        log.info("Médecin mis à jour: {}", savedMedecin.getEmail());

        // Gérer la photo: fichier uploadé (optionnel)
        handlePhoto(savedMedecin, photoFile);

        return userMapper.toResponse(savedMedecin);
    }

    /**
     * Supprime un médecin
     */
    @Transactional
    public void deleteMedecin(Long medecinId, Long adminId) {
        Utilisateur admin = validateAdminAndGetCabinet(adminId);
        Utilisateur medecin = findMedecinById(medecinId);
        validateMedecinInCabinet(medecin, admin.getCabinet());

        userRepository.delete(medecin);
        log.info("Médecin supprimé: {}", medecin.getEmail());
    }

    /**
     * Bascule le statut d'un médecin
     */
    @Transactional
    public UserResponse toggleMedecinStatus(Long medecinId, Long adminId) {
        Utilisateur admin = validateAdminAndGetCabinet(adminId);
        Utilisateur medecin = findMedecinById(medecinId);
        validateMedecinInCabinet(medecin, admin.getCabinet());

        medecin.setStatus(medecin.getStatus() == Status.ACTIF ? Status.INACTIF : Status.ACTIF);

        Utilisateur savedMedecin = userRepository.save(medecin);
        log.info("Statut du médecin {} basculé vers {}", savedMedecin.getEmail(), savedMedecin.getStatus());

        return userMapper.toResponse(savedMedecin);
    }

    // ==================== Méthodes de validation ====================

    private Utilisateur validateAdminAndGetCabinet(Long userId) {
        Utilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (user.getRole() != Role.ADMIN) {
            log.warn("L'utilisateur {} (rôle: {}) a tenté d'effectuer une action sans être ADMIN",
                    user.getEmail(), user.getRole());
            throw new BusinessException("Accès interdit - Réservé aux administrateurs");
        }

        if (user.getCabinet() == null) {
            throw new BusinessException("Vous n'avez pas de cabinet associé");
        }

        return user;
    }

    private void validateMedecinData(MedecinRequest request, Long cabinetId) {
        if (userRepository.existsByEmailAndCabinetId(request.email(), cabinetId)) {
            throw new BusinessException("Cet email est déjà utilisé par un utilisateur de ce cabinet");
        }

        if (userRepository.existsByTelephoneAndCabinetId(request.telephone(), cabinetId)) {
            throw new BusinessException("Ce téléphone est déjà utilisé par un utilisateur de ce cabinet");
        }
    }

    private void validateMedecinUpdate(MedecinRequest request, Utilisateur currentMedecin, Long cabinetId) {
        if (!currentMedecin.getEmail().equals(request.email())
                && userRepository.existsByEmailAndCabinetId(request.email(), cabinetId)) {
            throw new BusinessException("Cet email est déjà utilisé par un utilisateur de ce cabinet");
        }

        if (!currentMedecin.getTelephone().equals(request.telephone())
                && userRepository.existsByTelephoneAndCabinetId(request.telephone(), cabinetId)) {
            throw new BusinessException("Ce téléphone est déjà utilisé par un utilisateur de ce cabinet");
        }
    }

    private void validateSpecialiteInCabinet(Specialite specialite, Cabinet cabinet) {
        if (specialite.getCabinet() == null || !specialite.getCabinet().getId().equals(cabinet.getId())) {
            throw new BusinessException("La spécialité n'appartient pas à votre cabinet");
        }
    }

    private void validateMedecinInCabinet(Utilisateur medecin, Cabinet cabinet) {
        if (medecin.getCabinet() == null || !medecin.getCabinet().getId().equals(cabinet.getId())) {
            throw new BusinessException("Ce médecin n'appartient pas à votre cabinet");
        }
    }

    // ==================== Méthodes utilitaires ====================

    private Utilisateur findMedecinById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médecin non trouvé"));
    }

    private Specialite findSpecialiteById(Long id) {
        return specialiteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spécialité non trouvée"));
    }


}

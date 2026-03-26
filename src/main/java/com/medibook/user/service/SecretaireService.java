package com.medibook.user.service;

import com.medibook.cabinet.entity.Cabinet;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;
import com.medibook.common.event.SecretaireCreatedEvent;
import com.medibook.common.exception.BusinessException;
import com.medibook.common.exception.ResourceNotFoundException;
import com.medibook.common.storage.MediaUploadService;
import com.medibook.user.dto.SecretaireRequest;
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
 * Service pour la gestion des secrétaires
 * Accepte soit un fichier photo, soit une URL photo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SecretaireService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final MediaUploadService mediaUploadService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Crée un nouveau secretary pour le cabinet de l'admin
     * Accepte fichier photo OU URL photo
     */
    @Transactional
    public UserResponse createSecretaire(SecretaireRequest request, MultipartFile photoFile, Long adminId) {
        // 1. Valider que l'utilisateur est ADMIN et récupérer son cabinet
        Utilisateur admin = validateAdminAndGetCabinet(adminId);

        // 2. Valider les données du secrétaire
        validateSecretaireData(request, admin.getCabinet().getId());

        // 3. Créer le secrétaire
        Utilisateur secretaire = userMapper.toEntityFromSecretaire(request);
        secretaire.setCabinet(admin.getCabinet());
        secretaire.setRole(Role.SECRETAIRE);
        secretaire.setStatus(Status.ACTIF);
        secretaire.setMotDePasse(passwordEncoder.encode(request.motDePasse()));

        // 6. Sauvegarder d'abord pour avoir l'ID
        Utilisateur savedSecretaire = userRepository.save(secretaire);
        log.info("Secrétaire créé: {} pour le cabinet {}", savedSecretaire.getEmail(), admin.getCabinet().getNom());

        // 7. Gérer la photo: fichier uploadé vers Cloudinary
        handlePhotoCreation(savedSecretaire, photoFile);

        // 8. Publier l'événement pour envoyer l'email de création de compte
        eventPublisher.publishEvent(new SecretaireCreatedEvent(
                this,
                savedSecretaire,
                request.motDePasse(),
                admin.getCabinet().getNom()
        ));

        return userMapper.toResponse(savedSecretaire);
    }

    /**
     * Gère la photo lors de la création - upload asynchrone vers Cloudinary
     */
    private void handlePhotoCreation(Utilisateur secretaire, MultipartFile photoFile) {
        if (photoFile != null && !photoFile.isEmpty()) {
            String folder = "medibook/secretaires/" + secretaire.getId();
            final Long userId = secretaire.getId();
            mediaUploadService.uploadImageAsync(photoFile, folder, url -> {
                if (url != null) {
                    userRepository.findById(userId).ifPresent(user -> {
                        user.setPhoto(url);
                        userRepository.save(user);
                        log.info("Photo uploadée (async) pour le/la secrétaire {}: {}", user.getEmail(), url);
                    });
                }
            });
        }
    }

    /**
     * Gère la photo lors de la mise à jour:
     * - Si nouveau fichier → upload async + supprime l'ancienne
     * - Si rien de nouveau → conserve l'ancienne
     */
    private void handlePhoto(Utilisateur secretaire, MultipartFile photoFile) {
        if (photoFile != null && !photoFile.isEmpty()) {
            // Supprimer l'ancienne photo si elle existe
            String oldPhoto = secretaire.getPhoto();
            if (oldPhoto != null && !oldPhoto.isEmpty()) {
                mediaUploadService.deleteImageAsync(oldPhoto);
            }
            // Upload asynchrone
            String folder = "medibook/secretaires/" + secretaire.getId();
            final Long userId = secretaire.getId();
            mediaUploadService.uploadImageAsync(photoFile, folder, url -> {
                if (url != null) {
                    userRepository.findById(userId).ifPresent(user -> {
                        user.setPhoto(url);
                        userRepository.save(user);
                        log.info("Photo mise à jour (async) pour le/la secrétaire {}: {}", user.getEmail(), url);
                    });
                }
            });
        }
    }

    /**
     * Upload la photo de manière asynchrone
     */


    /**
     * Récupère tous les secrétaires du cabinet de l'admin
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getSecretairesByAdmin(Long adminId) {
        Utilisateur admin = validateAdminAndGetCabinet(adminId);
        return userRepository.findByRoleAndCabinetId(Role.SECRETAIRE, admin.getCabinet().getId()).stream()
                .map(userMapper::toResponse)
                .toList();
    }

    /**
     * Récupère un secretary par son ID
     */
    @Transactional(readOnly = true)
    public UserResponse getSecretaireById(Long secretaireId, Long adminId) {
        Utilisateur admin = validateAdminAndGetCabinet(adminId);
        Utilisateur secretaire = findSecretaireById(secretaireId);
        validateSecretaireInCabinet(secretaire, admin.getCabinet());
        return userMapper.toResponse(secretaire);
    }

    /**
     * Met à jour un secretary
     */
    @Transactional
    public UserResponse updateSecretaire(Long secretaireId, SecretaireRequest request, MultipartFile photoFile, Long adminId) {
        // 1. Valider que l'utilisateur est ADMIN
        Utilisateur admin = validateAdminAndGetCabinet(adminId);

        // 2. Récupérer le secretary
        Utilisateur secretaire = findSecretaireById(secretaireId);

        // 3. Valider que le secretary est dans le cabinet de l'admin
        validateSecretaireInCabinet(secretaire, admin.getCabinet());

        // 4. Valider les données pour la mise à jour
        validateSecretaireUpdate(request, secretaire, admin.getCabinet().getId());

        // 5. Mettre à jour les champs
        userMapper.updateFromSecretaireRequest(request, secretaire);

        // 6. Mettre à jour le mot de passe si fourni
        if (request.motDePasse() != null && !request.motDePasse().isEmpty()) {
            secretaire.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        }

        Utilisateur savedSecretaire = userRepository.save(secretaire);
        log.info("Secrétaire mis à jour: {}", savedSecretaire.getEmail());

        // 7. Gérer la photo: fichier uploadé
        handlePhoto(savedSecretaire, photoFile);

        return userMapper.toResponse(savedSecretaire);
    }

    /**
     * Supprime un secretary
     */
    @Transactional
    public void deleteSecretaire(Long secretaireId, Long adminId) {
        // 1. Valider que l'utilisateur est ADMIN
        Utilisateur admin = validateAdminAndGetCabinet(adminId);

        // 2. Récupérer le secretary
        Utilisateur secretaire = findSecretaireById(secretaireId);

        // 3. Valider que le secretary est dans le cabinet de l'admin
        validateSecretaireInCabinet(secretaire, admin.getCabinet());

        userRepository.delete(secretaire);
        log.info("Secrétaire supprimé: {}", secretaire.getEmail());
    }

    /**
     * Bascule le statut d'un secretary
     */
    @Transactional
    public UserResponse toggleSecretaireStatus(Long secretaireId, Long adminId) {
        // 1. Valider que l'utilisateur est ADMIN
        Utilisateur admin = validateAdminAndGetCabinet(adminId);

        // 2. Récupérer le secretary
        Utilisateur secretaire = findSecretaireById(secretaireId);

        // 3. Valider que le secretary est dans le cabinet de l'admin
        validateSecretaireInCabinet(secretaire, admin.getCabinet());

        // 4. Basculer le statut
        secretaire.setStatus(secretaire.getStatus() == Status.ACTIF ? Status.INACTIF : Status.ACTIF);

        Utilisateur savedSecretaire = userRepository.save(secretaire);
        log.info("Statut du/de la secretary {} basculé vers {}", savedSecretaire.getEmail(), savedSecretaire.getStatus());

        return userMapper.toResponse(savedSecretaire);
    }

    // ==================== Méthodes de validation ====================

    /**
     * Valide que l'utilisateur est ADMIN et retourne son cabinet
     */
    private Utilisateur validateAdminAndGetCabinet(Long userId) {
        Utilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(com.medibook.user.message.MessageErreur.UTILISATEUR_NON_TROUVE));

        if (user.getRole() != Role.ADMIN) {
            log.warn("L'utilisateur {} (rôle: {}) a tenté d'effectuer une action sans être ADMIN",
                    user.getEmail(), user.getRole());
            throw new BusinessException(com.medibook.user.message.MessageErreur.ACCES_ADMIN_SEUL);
        }

        if (user.getCabinet() == null) {
            throw new BusinessException(com.medibook.user.message.MessageErreur.PAS_DE_CABINET);
        }

        return user;
    }

    /**
     * Valide les données d'un nouveau secretary
     */
    private void validateSecretaireData(SecretaireRequest request, Long cabinetId) {
        if (userRepository.existsByEmailAndCabinetId(request.email(), cabinetId)) {
            throw new BusinessException(com.medibook.user.message.MessageErreur.EMAIL_DEJA_UTILISE_CABINET);
        }

        if (userRepository.existsByTelephoneAndCabinetId(request.telephone(), cabinetId)) {
            throw new BusinessException(com.medibook.user.message.MessageErreur.TELEPHONE_DEJA_UTILISE_CABINET);
        }
    }

    /**
     * Valide les données lors de la mise à jour
     */
    private void validateSecretaireUpdate(SecretaireRequest request, Utilisateur currentSecretaire, Long cabinetId) {
        // Vérifier si le nouvel email est déjà utilisé
        if (!currentSecretaire.getEmail().equals(request.email())
                && userRepository.existsByEmailAndCabinetId(request.email(), cabinetId)) {
            throw new BusinessException(com.medibook.user.message.MessageErreur.EMAIL_DEJA_UTILISE_CABINET);
        }

        // Vérifier si le nouveau téléphone est déjà utilisé
        if (!currentSecretaire.getTelephone().equals(request.telephone())
                && userRepository.existsByTelephoneAndCabinetId(request.telephone(), cabinetId)) {
            throw new BusinessException(com.medibook.user.message.MessageErreur.TELEPHONE_DEJA_UTILISE_CABINET);
        }
    }

    /**
     * Valide que le secretary appartient au cabinet
     */
    private void validateSecretaireInCabinet(Utilisateur secretaire, Cabinet cabinet) {
        if (secretaire.getCabinet() == null || !secretaire.getCabinet().getId().equals(cabinet.getId())) {
            throw new BusinessException(com.medibook.user.message.MessageErreur.SECRETAIRE_HORS_CABINET);
        }
    }

    // ==================== Méthodes utilitaires ====================

    /**
     * Trouve un secretary par ID
     */
    private Utilisateur findSecretaireById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(com.medibook.user.message.MessageErreur.SECRETAIRE_NON_TROUVE));
    }

}

package com.medibook.cabinet.service;

import com.medibook.cabinet.dto.CabinetCreateDTO;
import com.medibook.cabinet.dto.CabinetResponseDTO;
import com.medibook.cabinet.entity.Cabinet;
import com.medibook.cabinet.mapper.CabinetMapper;
import com.medibook.cabinet.message.MessageErreur;
import com.medibook.cabinet.repository.CabinetRepository;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;
import com.medibook.common.exception.FieldValidationException;
import com.medibook.common.exception.ResourceNotFoundException;
import com.medibook.common.exception.UnauthorizedException;
import com.medibook.common.storage.MediaUploadService;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service pour la gestion des cabinets
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CabinetService {

    private final CabinetRepository cabinetRepository;
    private final CabinetMapper cabinetMapper;
    private final UserRepository userRepository;
    private final MediaUploadService mediaUploadService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crée un nouveau cabinet avec logo optionnel
     * Seul le Super Admin peut créer un cabinet
     *
     * @param dto Les données du cabinet à créer
     * @param logo Fichier logo optionnel
     * @param userId L'ID de l'utilisateur qui effectue l'action
     * @return Le cabinet créé
     */
    @Transactional
    public CabinetResponseDTO createCabinet(CabinetCreateDTO dto, MultipartFile logo, Long userId) {
        // 1. Validation Super Admin
        Utilisateur superAdmin = validateSuperAdmin(userId);

        // 2. Validation complète avant toute écriture
        validateCreateCabinetRequest(dto);

        // 3. Créer le cabinet
        Cabinet cabinet = cabinetMapper.toEntity(dto);
        cabinet.setStatus(Cabinet.Status.ACTIF);
        
        // Utiliser URL logo fournie ou null (l'upload async se fait après)
        if (dto.logoUrl() != null && !dto.logoUrl().isEmpty()) {
            cabinet.setLogo(dto.logoUrl());
        }

        // 4. Sauvegarder le cabinet
        Cabinet savedCabinet = cabinetRepository.save(cabinet);
        log.info("Cabinet créé avec succès: {} par l'utilisateur {}", savedCabinet.getNom(), superAdmin.getEmail());

        // 5. Upload logo de manière asynchrone (hors transaction)
        if (logo != null && !logo.isEmpty()) {
            String folder = "medibook/cabinets/" + savedCabinet.getId();
            mediaUploadService.uploadImageAsync(logo, folder, url -> {
                if (url != null) {
                    savedCabinet.setLogo(url);
                    cabinetRepository.save(savedCabinet);
                    log.info("Logo uploadé pour le cabinet {}: {}", savedCabinet.getNom(), url);
                }
            });
        }

        // 6. Créer l'administrateur
        return createAdminAndBuildResponse(dto, savedCabinet);
    }

    /**
     * Valide que l'utilisateur est un Super Admin
     */
    private Utilisateur validateSuperAdmin(Long userId) {
        Utilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(com.medibook.cabinet.message.MessageErreur.CABINET_NON_TROUVE));

        if (user.getRole() != Role.SUPER_ADMIN) {
            log.warn("L'utilisateur {} (rôle: {}) a tenté de créer un cabinet sans authorization", 
                    user.getEmail(), user.getRole());
            throw new UnauthorizedException(MessageErreur.SEULEMENT_SUPER_ADMIN);
        }
        return user;
    }

    /**
     * Valide toutes les données de création du cabinet
     */
    private void validateCreateCabinetRequest(CabinetCreateDTO dto) {
        Map<String, String> errors = new LinkedHashMap<>();

        collectCabinetFieldErrors(dto, errors, null);
        collectAdminFieldErrors(dto, errors);

        if (!errors.isEmpty()) {
            throw new FieldValidationException("Veuillez corriger les champs en erreur", errors);
        }
    }

    /**
     * Crée l'administrateur du cabinet et construit la réponse
     */
    private CabinetResponseDTO createAdminAndBuildResponse(CabinetCreateDTO dto, Cabinet savedCabinet) {
        // Créer l'utilisateur ADMIN
        Utilisateur admin = Utilisateur.builder()
                .nom(dto.adminNom())
                .prenom(dto.adminPrenom())
                .email(dto.adminEmail())
                .telephone(dto.adminTelephone())
                .motDePasse(passwordEncoder.encode(dto.adminPassword()))
                .role(Role.ADMIN)
                .status(Status.ACTIF)
                .cabinet(savedCabinet)
                .build();

        userRepository.save(admin);
        log.info("Administrateur créé pour le cabinet {}: {}", savedCabinet.getNom(), admin.getEmail());

        // Mapper le cabinet vers DTO avec les infos de l'admin
        return buildResponseWithAdmin(savedCabinet, admin);
    }

    /**
     * Construit la réponse avec les infos de l'admin
     */
    private CabinetResponseDTO buildResponseWithAdmin(Cabinet cabinet, Utilisateur admin) {
        // Utiliser le mapper pour la conversion de base
        CabinetResponseDTO response = cabinetMapper.toResponseDTO(cabinet);
        
        // Ajouter les infos de l'admin manuellement car le record est immutable
        return new CabinetResponseDTO(
                response.id(),
                response.nom(),
                response.logo(),
                response.couleurPrimaire(),
                response.couleurSecondaire(),
                response.adresse(),
                response.telephone(),
                response.email(),
                response.status(),
                new CabinetResponseDTO.AdminInfo(
                        admin.getId(),
                        admin.getNom(),
                        admin.getPrenom(),
                        admin.getEmail(),
                        admin.getTelephone()
                )
        );
    }

    /**
     * Ajoute les erreurs métier liées au cabinet
     */
    private void collectCabinetFieldErrors(CabinetCreateDTO dto, Map<String, String> errors, Cabinet currentCabinet) {
        if (dto.nom() != null && !dto.nom().isBlank()) {
            boolean nomChanged = currentCabinet == null || !dto.nom().equals(currentCabinet.getNom());
            if (nomChanged && cabinetRepository.existsByNom(dto.nom())) {
                errors.put("nom", MessageErreur.CABINET_DEJA_EXISTANT);
            }
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            boolean emailChanged = currentCabinet == null || !dto.email().equals(currentCabinet.getEmail());
            if (emailChanged && cabinetRepository.existsByEmail(dto.email())) {
                errors.put("email", MessageErreur.EMAIL_DEJA_UTILISE);
            }
        }

        if (dto.telephone() != null && !dto.telephone().isBlank()) {
            boolean telephoneChanged = currentCabinet == null || !dto.telephone().equals(currentCabinet.getTelephone());
            if (telephoneChanged && cabinetRepository.existsByTelephone(dto.telephone())) {
                errors.put("telephone", MessageErreur.TELEPHONE_DEJA_UTILISE);
            }
        }
    }

    /**
     * Ajoute les erreurs métier liées à l'administrateur
     */
    private void collectAdminFieldErrors(CabinetCreateDTO dto, Map<String, String> errors) {
        if (dto.adminEmail() != null && !dto.adminEmail().isBlank() && userRepository.existsByEmail(dto.adminEmail())) {
            errors.put("adminEmail", MessageErreur.ADMIN_EMAIL_DEJA_UTILISE);
        }

        if (dto.adminTelephone() != null && !dto.adminTelephone().isBlank() && userRepository.existsByTelephone(dto.adminTelephone())) {
            errors.put("adminTelephone", MessageErreur.ADMIN_TELEPHONE_DEJA_UTILISE);
        }
    }

    /**
     * Récupère tous les cabinets
     */
    @Transactional(readOnly = true)
    public List<CabinetResponseDTO> getAllCabinets() {
        return cabinetRepository.findAll().stream()
                .map(cabinetMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<CabinetResponseDTO> getAllCabinets(Pageable pageable) {
        return cabinetRepository.findAll(pageable)
                .map(cabinetMapper::toResponseDTO);
    }

    /**
     * Récupère un cabinet par son ID
     */
    @Transactional(readOnly = true)
    public CabinetResponseDTO getCabinetById(Long id) {
        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageErreur.CABINET_NON_TROUVE));
        return cabinetMapper.toResponseDTO(cabinet);
    }

    /**
     * Met à jour un cabinet
     */
    @Transactional
    public CabinetResponseDTO updateCabinet(Long id, CabinetCreateDTO dto, MultipartFile logo, Long userId) {
        // 1. Validation Super Admin
        validateSuperAdmin(userId);
        
        // 2. Récupérer et valider le cabinet
        Cabinet cabinet = findCabinetById(id);
        
        // 3. Valider les nouvelles données
        validateCabinetUpdate(dto, cabinet);
        
        // 4. Mettre à jour le cabinet
        cabinetMapper.updateFromDTO(dto, cabinet);

        // 5. Upload logo async si présent
        if (logo != null && !logo.isEmpty()) {
            String folder = "medibook/cabinets/" + cabinet.getId();
            mediaUploadService.uploadImageAsync(logo, folder, url -> {
                if (url != null) {
                    cabinet.setLogo(url);
                    cabinetRepository.save(cabinet);
                    log.info("Logo mis à jour pour le cabinet {}: {}", cabinet.getNom(), url);
                }
            });
        }

        Cabinet savedCabinet = cabinetRepository.save(cabinet);
        log.info("Cabinet mis à jour: {}", savedCabinet.getNom());

        return cabinetMapper.toResponseDTO(savedCabinet);
    }

    /**
     * Valide les données lors de la mise à jour
     */
    private void validateCabinetUpdate(CabinetCreateDTO dto, Cabinet currentCabinet) {
        Map<String, String> errors = new LinkedHashMap<>();
        collectCabinetFieldErrors(dto, errors, currentCabinet);

        if (!errors.isEmpty()) {
            throw new FieldValidationException("Veuillez corriger les champs en erreur", errors);
        }
    }

    /**
     * Supprime un cabinet
     */
    @Transactional
    public void deleteCabinet(Long id, Long userId) {
        // 1. Validation Super Admin
        validateSuperAdmin(userId);
        
        // 2. Récupérer le cabinet
        Cabinet cabinet = findCabinetById(id);

        cabinetRepository.delete(cabinet);
        log.info("Cabinet supprimé: {}", cabinet.getNom());
    }

    /**
     * Bascule le statut d'un cabinet
     */
    @Transactional
    public CabinetResponseDTO toggleCabinetStatus(Long id, Long userId) {
        // 1. Validation Super Admin
        validateSuperAdmin(userId);
        
        // 2. Récupérer le cabinet
        Cabinet cabinet = findCabinetById(id);

        cabinet.setStatus(cabinet.getStatus() == Cabinet.Status.ACTIF ? 
                Cabinet.Status.INACTIF : Cabinet.Status.ACTIF);

        Cabinet savedCabinet = cabinetRepository.save(cabinet);
        log.info("Statut du cabinet {} basculé vers {}", savedCabinet.getNom(), savedCabinet.getStatus());

        return cabinetMapper.toResponseDTO(savedCabinet);
    }

    /**
     * Met à jour le logo d'un cabinet
     */
    @Transactional
    public CabinetResponseDTO updateLogo(Long id, MultipartFile logo, Long userId) {
        // 1. Validation Super Admin
        validateSuperAdmin(userId);
        
        // 2. Récupérer le cabinet
        Cabinet cabinet = findCabinetById(id);

        // 3. Upload async
        String folder = "medibook/cabinets/" + cabinet.getId();
        mediaUploadService.uploadImageAsync(logo, folder, url -> {
            if (url != null) {
                cabinet.setLogo(url);
                cabinetRepository.save(cabinet);
                log.info("Logo mis à jour pour le cabinet {}: {}", cabinet.getNom(), url);
            }
        });

        return cabinetMapper.toResponseDTO(cabinet);
    }
    
    /**
     * Trouve un cabinet par ID ou throw une exception
     */
    private Cabinet findCabinetById(Long id) {
        return cabinetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageErreur.CABINET_NON_TROUVE));
    }
}

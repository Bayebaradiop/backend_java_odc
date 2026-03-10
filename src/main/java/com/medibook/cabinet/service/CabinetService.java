package com.medibook.cabinet.service;

import com.medibook.cabinet.dto.CabinetCreateDTO;
import com.medibook.cabinet.dto.CabinetResponseDTO;
import com.medibook.cabinet.entity.Cabinet;
import com.medibook.cabinet.mapper.CabinetMapper;
import com.medibook.cabinet.message.MessageErreur;
import com.medibook.cabinet.repository.CabinetRepository;
import com.medibook.common.enums.Role;
import com.medibook.common.exception.BusinessException;
import com.medibook.common.exception.ResourceNotFoundException;
import com.medibook.common.exception.UnauthorizedException;
import com.medibook.common.storage.StorageService;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    private final StorageService storageService;

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
        // Vérifier que l'utilisateur est un Super Admin
        Utilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (user.getRole() != Role.SUPER_ADMIN) {
            log.warn("L'utilisateur {} (rôle: {}) a tenté de créer un cabinet sans authorization", 
                    user.getEmail(), user.getRole());
            throw new UnauthorizedException(MessageErreur.SEULEMENT_SUPER_ADMIN);
        }

        // Vérifier si un cabinet avec le même nom existe déjà
        if (cabinetRepository.existsByNom(dto.nom())) {
            throw new BusinessException(MessageErreur.CABINET_DEJA_EXISTANT);
        }

        // Vérifier si l'email est déjà utilisé
        if (cabinetRepository.existsByEmail(dto.email())) {
            throw new BusinessException(MessageErreur.EMAIL_DEJA_UTILISE);
        }

        // Créer le cabinet
        Cabinet cabinet = cabinetMapper.toEntity(dto);
        cabinet.setStatus(Cabinet.Status.ACTIF);

        // Gérer le logo (soit fichier uploadé, soit URL fournie)
        String logoUrl = null;
        
        // 1. Upload du fichier logo si présent
        if (logo != null && !logo.isEmpty()) {
            try {
                logoUrl = storageService.uploadFile(logo.getBytes(), 
                        dto.nom().replace(" ", "_") + "_logo", 
                        "medibook/cabinets/logos");
                log.info("Logo uploadé: {}", logoUrl);
            } catch (Exception e) {
                log.error("Erreur upload logo: {}", e.getMessage(), e);
                throw new BusinessException(MessageErreur.ERREUR_UPLOAD_LOGO);
            }
        }
        // 2. Sinon utiliser l'URL logo fournie dans le DTO
        else if (dto.logoUrl() != null && !dto.logoUrl().isEmpty()) {
            logoUrl = dto.logoUrl();
        }

        cabinet.setLogo(logoUrl);

        Cabinet savedCabinet = cabinetRepository.save(cabinet);
        log.info("Cabinet créé avec succès: {} par l'utilisateur {}", savedCabinet.getNom(), user.getEmail());

        return cabinetMapper.toResponseDTO(savedCabinet);
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
     * Met à jour un cabinet avec logo optionnel
     */
    @Transactional
    public CabinetResponseDTO updateCabinet(Long id, CabinetCreateDTO dto, MultipartFile logo, Long userId) {
        // Vérifier que l'utilisateur est un Super Admin
        Utilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (user.getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException(MessageErreur.ACCES_INTERDIT);
        }

        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageErreur.CABINET_NON_TROUVE));

        // Vérifier si le nouveau nom est déjà utilisé par un autre cabinet
        if (!cabinet.getNom().equals(dto.nom()) && cabinetRepository.existsByNom(dto.nom())) {
            throw new BusinessException(MessageErreur.CABINET_DEJA_EXISTANT);
        }

        // Vérifier si le nouvel email est déjà utilisé par un autre cabinet
        if (!cabinet.getEmail().equals(dto.email()) && cabinetRepository.existsByEmail(dto.email())) {
            throw new BusinessException(MessageErreur.EMAIL_DEJA_UTILISE);
        }

        // Mettre à jour les champs de base
        cabinetMapper.updateFromDTO(dto, cabinet);

        // Gérer le logo (nouveau fichier, nouvelle URL, ou pas de changement)
        if (logo != null && !logo.isEmpty()) {
            // Supprimer l'ancien logo si présent
            if (cabinet.getLogo() != null && !cabinet.getLogo().isEmpty()) {
                try {
                    String oldPublicId = storageService.extractPublicId(cabinet.getLogo());
                    if (oldPublicId != null) {
                        storageService.deleteFile(oldPublicId);
                    }
                } catch (Exception e) {
                    log.error("Erreur lors de la suppression de l'ancien logo: {}", e.getMessage());
                }
            }
            
            // Upload du nouveau logo
            try {
                String newLogoUrl = storageService.uploadFile(logo.getBytes(), 
                        dto.nom().replace(" ", "_") + "_logo", 
                        "medibook/cabinets/logos");
                cabinet.setLogo(newLogoUrl);
            } catch (Exception e) {
                log.error("Erreur lors de l'upload du logo: {}", e.getMessage());
                throw new BusinessException(MessageErreur.ERREUR_UPLOAD_LOGO);
            }
        } else if (dto.logoUrl() != null && !dto.logoUrl().isEmpty()) {
            // Nouvelle URL logo fournie
            cabinet.setLogo(dto.logoUrl());
        }

        Cabinet updatedCabinet = cabinetRepository.save(cabinet);
        log.info("Cabinet mis à jour: {}", updatedCabinet.getNom());

        return cabinetMapper.toResponseDTO(updatedCabinet);
    }

    /**
     * Supprime un cabinet
     */
    @Transactional
    public void deleteCabinet(Long id, Long userId) {
        // Vérifier que l'utilisateur est un Super Admin
        Utilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (user.getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException(MessageErreur.ACCES_INTERDIT);
        }

        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageErreur.CABINET_NON_TROUVE));

        // Supprimer le logo si présent
        if (cabinet.getLogo() != null && !cabinet.getLogo().isEmpty()) {
            try {
                String publicId = storageService.extractPublicId(cabinet.getLogo());
                if (publicId != null) {
                    storageService.deleteFile(publicId);
                }
            } catch (Exception e) {
                log.error("Erreur lors de la suppression du logo: {}", e.getMessage());
            }
        }

        cabinetRepository.delete(cabinet);
        log.info("Cabinet supprimé: {}", cabinet.getNom());
    }

    /**
     * Active ou désactive un cabinet
     */
    @Transactional
    public CabinetResponseDTO toggleCabinetStatus(Long id, Long userId) {
        // Vérifier que l'utilisateur est un Super Admin
        Utilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (user.getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException(MessageErreur.ACCES_INTERDIT);
        }

        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageErreur.CABINET_NON_TROUVE));

        cabinet.setStatus(cabinet.getStatus() == Cabinet.Status.ACTIF 
                ? Cabinet.Status.INACTIF 
                : Cabinet.Status.ACTIF);

        Cabinet updatedCabinet = cabinetRepository.save(cabinet);
        log.info("Statut du cabinet {} basculé vers: {}", updatedCabinet.getNom(), updatedCabinet.getStatus());

        return cabinetMapper.toResponseDTO(updatedCabinet);
    }

    /**
     * Met à jour le logo d'un cabinet
     */
    @Transactional
    public CabinetResponseDTO updateLogo(Long id, MultipartFile logo, Long userId) {
        // Vérifier que l'utilisateur est un Super Admin
        Utilisateur user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (user.getRole() != Role.SUPER_ADMIN) {
            throw new UnauthorizedException(MessageErreur.ACCES_INTERDIT);
        }

        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MessageErreur.CABINET_NON_TROUVE));

        // Supprimer l'ancien logo si présent
        if (cabinet.getLogo() != null && !cabinet.getLogo().isEmpty()) {
            try {
                String oldPublicId = storageService.extractPublicId(cabinet.getLogo());
                if (oldPublicId != null) {
                    storageService.deleteFile(oldPublicId);
                }
            } catch (Exception e) {
                log.error("Erreur lors de la suppression de l'ancien logo: {}", e.getMessage());
            }
        }

        // Upload du nouveau logo
        String logoUrl = null;
        if (logo != null && !logo.isEmpty()) {
            try {
                logoUrl = storageService.uploadFile(logo.getBytes(), 
                        cabinet.getNom().replace(" ", "_") + "_logo", 
                        "medibook/cabinets/logos");
            } catch (Exception e) {
                log.error("Erreur lors de l'upload du logo: {}", e.getMessage());
                throw new BusinessException(MessageErreur.ERREUR_UPLOAD_LOGO);
            }
        }

        cabinet.setLogo(logoUrl);
        Cabinet updatedCabinet = cabinetRepository.save(cabinet);
        log.info("Logo du cabinet {} mis à jour", updatedCabinet.getNom());

        return cabinetMapper.toResponseDTO(updatedCabinet);
    }
}

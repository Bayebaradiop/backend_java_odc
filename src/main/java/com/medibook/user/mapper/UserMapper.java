package com.medibook.user.mapper;

import com.medibook.user.dto.MedecinRequest;
import com.medibook.user.dto.SecretaireRequest;
import com.medibook.user.dto.UserResponse;
import com.medibook.user.entity.Utilisateur;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper pour la conversion entre Utilisateur et DTOs
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    /**
     * Convertit une entity Utilisateur en UserResponse
     */
    @Mapping(source = "cabinet.id", target = "cabinetId")
    @Mapping(source = "cabinet.nom", target = "cabinetNom")
    @Mapping(source = "specialite.id", target = "specialiteId")
    @Mapping(source = "specialite.nom", target = "specialiteNom")
    UserResponse toResponse(Utilisateur utilisateur);

    /**
     * Convertit un MedecinRequest en entity Utilisateur (sans les relations)
     * Ignore photo (MultipartFile) car géré séparément
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cabinet", ignore = true)
    @Mapping(target = "specialite", ignore = true)
    @Mapping(target = "plannings", ignore = true)
    @Mapping(target = "rendezVousMedecin", ignore = true)
    @Mapping(target = "rendezVousPatient", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "photo", ignore = true)
    @Mapping(target = "motDePasse", ignore = true)
    Utilisateur toEntityFromMedecin(MedecinRequest request);

    /**
     * Convertit un SecretaireRequest en entity Utilisateur (sans les relations)
     * Ignore photo (MultipartFile) car géré séparément
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cabinet", ignore = true)
    @Mapping(target = "specialite", ignore = true)
    @Mapping(target = "plannings", ignore = true)
    @Mapping(target = "rendezVousMedecin", ignore = true)
    @Mapping(target = "rendezVousPatient", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "photo", ignore = true)
    @Mapping(target = "motDePasse", ignore = true)
    Utilisateur toEntityFromSecretaire(SecretaireRequest request);

    /**
     * Met à jour un Utilisateur à partir d'un MedecinRequest
     * Ignore photo (MultipartFile) car géré séparément
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cabinet", ignore = true)
    @Mapping(target = "specialite", ignore = true)
    @Mapping(target = "plannings", ignore = true)
    @Mapping(target = "rendezVousMedecin", ignore = true)
    @Mapping(target = "rendezVousPatient", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "photo", ignore = true)
    @Mapping(target = "motDePasse", ignore = true)
    void updateFromMedecinRequest(MedecinRequest request, @MappingTarget Utilisateur utilisateur);

    /**
     * Met à jour un Utilisateur à partir d'un SecretaireRequest
     * Ignore photo (MultipartFile) car géré séparément
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cabinet", ignore = true)
    @Mapping(target = "specialite", ignore = true)
    @Mapping(target = "plannings", ignore = true)
    @Mapping(target = "rendezVousMedecin", ignore = true)
    @Mapping(target = "rendezVousPatient", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "photo", ignore = true)
    @Mapping(target = "motDePasse", ignore = true)
    void updateFromSecretaireRequest(SecretaireRequest request, @MappingTarget Utilisateur utilisateur);
}

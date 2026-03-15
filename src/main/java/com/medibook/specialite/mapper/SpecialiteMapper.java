package com.medibook.specialite.mapper;

import com.medibook.specialite.dto.SpecialiteRequest;
import com.medibook.specialite.dto.SpecialiteResponse;
import com.medibook.specialite.entity.Specialite;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper pour la conversion entre Specialite, SpecialiteRequest et SpecialiteResponse
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface SpecialiteMapper {

    /**
     * Convertit un SpecialiteRequest en entity Specialite
     */
    @Mapping(target = "cabinet", ignore = true)
    @Mapping(target = "id", ignore = true)
    Specialite toEntity(SpecialiteRequest request);

    /**
     * Convertit une entity Specialite en SpecialiteResponse
     */
    @Mapping(source = "cabinet.nom", target = "cabinetNom")
    @Mapping(source = "cabinet.id", target = "cabinetId")
    SpecialiteResponse toResponseDTO(Specialite specialite);

    /**
     * Met à jour une Specialite existante à partir d'un SpecialiteRequest
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromRequest(SpecialiteRequest request, @MappingTarget Specialite specialite);
}

package com.medibook.cabinet.mapper;

import com.medibook.cabinet.dto.CabinetCreateDTO;
import com.medibook.cabinet.dto.CabinetResponseDTO;
import com.medibook.cabinet.entity.Cabinet;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * Mapper pour la conversion entre Cabinet, CabinetCreateDTO et CabinetResponseDTO
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CabinetMapper {

    /**
     * Convertit un CabinetCreateDTO en entity Cabinet
     */
    Cabinet toEntity(CabinetCreateDTO dto);

    /**
     * Convertit une entity Cabinet en CabinetResponseDTO
     */
    CabinetResponseDTO toResponseDTO(Cabinet cabinet);

    /**
     * Met à jour un Cabinet existant à partir d'un CabinetCreateDTO
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDTO(CabinetCreateDTO dto, @MappingTarget Cabinet cabinet);
}

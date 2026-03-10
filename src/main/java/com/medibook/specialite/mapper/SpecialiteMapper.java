package com.medibook.specialite.mapper;

import org.springframework.stereotype.Component;

import com.medibook.specialite.dto.SpecialiteResponse;
import com.medibook.specialite.entity.Specialite;

@Component
public class SpecialiteMapper {

    /**
     * Transforme une Specialite en SpecialiteResponse
     */
    public SpecialiteResponse toSpecialiteResponse(Specialite specialite) {
        if (specialite == null) {
            return null;
        }
        
        return SpecialiteResponse.builder()
                .id(specialite.getId())
                .nom(specialite.getNom())
                .description(specialite.getDescription())
                .cabinetId(specialite.getCabinet() != null ? specialite.getCabinet().getId() : null)
                .cabinetNom(specialite.getCabinet() != null ? specialite.getCabinet().getNom() : null)
                .build();
    }
}

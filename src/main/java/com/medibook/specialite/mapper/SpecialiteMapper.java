package com.medibook.specialite.mapper;

import org.springframework.stereotype.Component;

import com.medibook.specialite.dto.SpecialiteRequest;
import com.medibook.specialite.dto.SpecialiteResponse;
import com.medibook.specialite.entity.Specialite;

@Component
public class SpecialiteMapper {

    /**
     * Transforme une Specialite en SpecialiteResponse (utilisé par bara_dev)
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

    /**
     * Transforme un SpecialiteRequest en entité Specialite (utilisé par lydevtech)
     */
    public Specialite toEntity(SpecialiteRequest request) {
        if (request == null) {
            return null;
        }

        return Specialite.builder()
                .nom(request.nom())
                .description(request.description())
                .build();
    }

    /**
     * Transforme une Specialite en SpecialiteResponse (alias utilisé par lydevtech / SpecialiteService)
     */
    public SpecialiteResponse toResponseDTO(Specialite specialite) {
        return toSpecialiteResponse(specialite);
    }

    /**
     * Met à jour une Specialite existante à partir d'un SpecialiteRequest
     */
    public void updateFromRequest(SpecialiteRequest request, Specialite specialite) {
        if (request == null || specialite == null) {
            return;
        }

        specialite.setNom(request.nom());
        specialite.setDescription(request.description());
    }
}

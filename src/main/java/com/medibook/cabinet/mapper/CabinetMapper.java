package com.medibook.cabinet.mapper;

import org.springframework.stereotype.Component;

import com.medibook.cabinet.dto.CabinetCreateDTO;
import com.medibook.cabinet.dto.CabinetResponse;
import com.medibook.cabinet.dto.CabinetResponseDTO;
import com.medibook.cabinet.entity.Cabinet;

@Component
public class CabinetMapper {

    /**
     * Transforme un Cabinet en CabinetResponse (utilisé par bara_dev / PatientSearchService)
     */
    public CabinetResponse toCabinetResponse(Cabinet cabinet) {
        if (cabinet == null) {
            return null;
        }
        
        return CabinetResponse.builder()
                .id(cabinet.getId())
                .nom(cabinet.getNom())
                .logo(cabinet.getLogo())
                .adresse(cabinet.getAdresse())
                .telephone(cabinet.getTelephone())
                .email(cabinet.getEmail())
                .couleurPrimaire(cabinet.getCouleurPrimaire())
                .couleurSecondaire(cabinet.getCouleurSecondaire())
                .build();
    }

    /**
     * Transforme un CabinetCreateDTO en entité Cabinet (utilisé par lydevtech / CabinetService)
     */
    public Cabinet toEntity(CabinetCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return Cabinet.builder()
                .nom(dto.nom())
                .adresse(dto.adresse())
                .telephone(dto.telephone())
                .email(dto.email())
                .couleurPrimaire(dto.couleurPrimaire())
                .couleurSecondaire(dto.couleurSecondaire())
                .build();
    }

    /**
     * Transforme un Cabinet en CabinetResponseDTO (utilisé par lydevtech / CabinetService)
     */
    public CabinetResponseDTO toResponseDTO(Cabinet cabinet) {
        if (cabinet == null) {
            return null;
        }

        return new CabinetResponseDTO(
                cabinet.getId(),
                cabinet.getNom(),
                cabinet.getLogo(),
                cabinet.getCouleurPrimaire(),
                cabinet.getCouleurSecondaire(),
                cabinet.getAdresse(),
                cabinet.getTelephone(),
                cabinet.getEmail(),
                cabinet.getStatus() != null ? cabinet.getStatus().name() : null
        );
    }

    /**
     * Met à jour un Cabinet existant à partir d'un CabinetCreateDTO
     */
    public void updateFromDTO(CabinetCreateDTO dto, Cabinet cabinet) {
        if (dto == null || cabinet == null) {
            return;
        }

        cabinet.setNom(dto.nom());
        cabinet.setAdresse(dto.adresse());
        cabinet.setTelephone(dto.telephone());
        cabinet.setEmail(dto.email());
        cabinet.setCouleurPrimaire(dto.couleurPrimaire());
        cabinet.setCouleurSecondaire(dto.couleurSecondaire());

        if (dto.logoUrl() != null && !dto.logoUrl().isEmpty()) {
            cabinet.setLogo(dto.logoUrl());
        }
    }
}

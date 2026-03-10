package com.medibook.cabinet.mapper;

import org.springframework.stereotype.Component;

import com.medibook.cabinet.dto.CabinetResponse;
import com.medibook.cabinet.entity.Cabinet;

@Component
public class CabinetMapper {

    /**
     * Transforme un Cabinet en CabinetResponse
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
}

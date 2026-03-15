package com.medibook.planning.mapper;

import com.medibook.planning.dto.PlanningResponse;
import com.medibook.planning.entity.TemplateSemaine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface PlanningMapper {

    @Mapping(target = "medecinId", source = "medecin", qualifiedByName = "medecinId")
    @Mapping(target = "medecinNom", source = "medecin", qualifiedByName = "fullName")
    @Mapping(target = "heureDebut", source = "heureDebut", qualifiedByName = "timeToString")
    @Mapping(target = "heureFin", source = "heureFin", qualifiedByName = "timeToString")
    PlanningResponse toResponse(TemplateSemaine template);

    @Named("fullName")
    default String getFullName(com.medibook.user.entity.Utilisateur medecin) {
        if (medecin == null) return null;
        return medecin.getPrenom() + " " + medecin.getNom();
    }

    @Named("medecinId")
    default Long getMedecinId(com.medibook.user.entity.Utilisateur medecin) {
        if (medecin == null) return null;
        return medecin.getId();
    }

    @Named("timeToString")
    default String timeToString(LocalTime time) {
        if (time == null) return null;
        return time.toString();
    }
}

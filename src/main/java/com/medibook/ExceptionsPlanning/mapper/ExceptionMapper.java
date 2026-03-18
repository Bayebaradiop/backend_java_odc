package com.medibook.ExceptionsPlanning.mapper;

import com.medibook.ExceptionsPlanning.dto.ExceptionResponse;
import com.medibook.ExceptionsPlanning.entity.ExceptionsPlanning;
import com.medibook.user.entity.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalTime;

@Mapper(componentModel = "spring")
public interface ExceptionMapper {

    @Mapping(target = "medecinId", source = "medecin", qualifiedByName = "medecinId")
    @Mapping(target = "medecinNom", source = "medecin", qualifiedByName = "fullName")
    @Mapping(target = "heureDebut", source = "heureDebut", qualifiedByName = "timeToString")
    @Mapping(target = "heureFin", source = "heureFin", qualifiedByName = "timeToString")
    ExceptionResponse toResponse(ExceptionsPlanning exception);

    @Named("fullName")
    default String getFullName(Utilisateur medecin) {
        if (medecin == null) return null;
        return medecin.getPrenom() + " " + medecin.getNom();
    }

    @Named("medecinId")
    default Long getMedecinId(Utilisateur medecin) {
        if (medecin == null) return null;
        return medecin.getId();
    }

    @Named("timeToString")
    default String timeToString(LocalTime time) {
        if (time == null) return null;
        return time.toString();
    }
}

package com.medibook.ExceptionsPlanning.mapper;

import com.medibook.ExceptionsPlanning.dto.ExceptionResponse;
import com.medibook.ExceptionsPlanning.entity.ExceptionsPlanning;
import org.springframework.stereotype.Component;

@Component
public class ExceptionMapper {

    public ExceptionResponse toResponse(ExceptionsPlanning exception) {
        if (exception == null) return null;
        return new ExceptionResponse(
                exception.getId(),
                exception.getMedecin() != null ? exception.getMedecin().getId() : null,
                exception.getMedecin() != null
                        ? exception.getMedecin().getPrenom() + " " + exception.getMedecin().getNom()
                        : null,
                exception.getDateDebut(),
                exception.getDateFin(),
                exception.getType(),
                exception.getHeureDebut() != null ? exception.getHeureDebut().toString() : null,
                exception.getHeureFin() != null ? exception.getHeureFin().toString() : null,
                exception.getMotif()
        );
    }
}

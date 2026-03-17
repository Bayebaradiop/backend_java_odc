package com.medibook.user.mapper;

import org.springframework.stereotype.Component;

import com.medibook.user.dto.MedecinResponse;
import com.medibook.user.dto.UserResponse;
import com.medibook.user.entity.Utilisateur;

@Component
public class UserMapper {

    /**
     * Transforme un Utilisateur (médecin) en MedecinResponse
     */
    public MedecinResponse toMedecinResponse(Utilisateur medecin) {
        if (medecin == null) {
            return null;
        }
        
        return MedecinResponse.builder()
                .id(medecin.getId())
                .prenom(medecin.getPrenom())
                .nom(medecin.getNom())
                .photo(medecin.getPhoto())
                .telephone(medecin.getTelephone())
                .email(medecin.getEmail())
                .specialiteId(medecin.getSpecialite() != null ? medecin.getSpecialite().getId() : null)
                .specialiteNom(medecin.getSpecialite() != null ? medecin.getSpecialite().getNom() : null)
                .cabinetId(medecin.getCabinet() != null ? medecin.getCabinet().getId() : null)
                .cabinetNom(medecin.getCabinet() != null ? medecin.getCabinet().getNom() : null)
                .build();
    }

    /**
     * Transforme un Utilisateur en UserResponse
     */
    public UserResponse toResponse(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        return UserResponse.builder()
                .id(utilisateur.getId())
                .prenom(utilisateur.getPrenom())
                .nom(utilisateur.getNom())
                .email(utilisateur.getEmail())
                .telephone(utilisateur.getTelephone())
                .photo(utilisateur.getPhoto())
                .role(utilisateur.getRole())
                .status(utilisateur.getStatus())
                .cabinetId(utilisateur.getCabinet() != null ? utilisateur.getCabinet().getId() : null)
                .cabinetNom(utilisateur.getCabinet() != null ? utilisateur.getCabinet().getNom() : null)
                .specialiteId(utilisateur.getSpecialite() != null ? utilisateur.getSpecialite().getId() : null)
                .specialiteNom(utilisateur.getSpecialite() != null ? utilisateur.getSpecialite().getNom() : null)
                .build();
    }

    /**
     * Met à jour un Utilisateur à partir d'un MedecinRequest (champs simples)
     */
    public void updateFromMedecinRequest(com.medibook.user.dto.MedecinRequest request, Utilisateur utilisateur) {
        if (request.prenom() != null) utilisateur.setPrenom(request.prenom());
        if (request.nom() != null) utilisateur.setNom(request.nom());
        if (request.email() != null) utilisateur.setEmail(request.email());
        if (request.telephone() != null) utilisateur.setTelephone(request.telephone());
    }

    /**
     * Convertit un MedecinRequest en entity Utilisateur
     */
    public Utilisateur toEntityFromMedecin(com.medibook.user.dto.MedecinRequest request) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom(request.prenom());
        utilisateur.setNom(request.nom());
        utilisateur.setEmail(request.email());
        utilisateur.setTelephone(request.telephone());
        return utilisateur;
    }

    /**
     * Convertit un SecretaireRequest en entity Utilisateur
     */
    public Utilisateur toEntityFromSecretaire(com.medibook.user.dto.SecretaireRequest request) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setPrenom(request.prenom());
        utilisateur.setNom(request.nom());
        utilisateur.setEmail(request.email());
        utilisateur.setTelephone(request.telephone());
        return utilisateur;
    }

    /**
     * Met à jour un Utilisateur à partir d'un SecretaireRequest
     */
    public void updateFromSecretaireRequest(com.medibook.user.dto.SecretaireRequest request, Utilisateur utilisateur) {
        if (request.prenom() != null) utilisateur.setPrenom(request.prenom());
        if (request.nom() != null) utilisateur.setNom(request.nom());
        if (request.email() != null) utilisateur.setEmail(request.email());
        if (request.telephone() != null) utilisateur.setTelephone(request.telephone());
    }
}

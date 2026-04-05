package com.medibook.patient.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.medibook.cabinet.dto.CabinetResponse;
import com.medibook.cabinet.entity.Cabinet;
import com.medibook.cabinet.mapper.CabinetMapper;
import com.medibook.cabinet.repository.CabinetRepository;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;
import com.medibook.patient.message.MessageErreur;
import com.medibook.specialite.dto.SpecialiteResponse;
import com.medibook.specialite.mapper.SpecialiteMapper;
import com.medibook.specialite.repository.SpecialiteRepository;
import com.medibook.user.dto.MedecinResponse;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.mapper.UserMapper;
import com.medibook.user.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientSearchService {

    private final CabinetRepository cabinetRepository;
    private final SpecialiteRepository specialiteRepository;
    private final UserRepository userRepository;
    private final CabinetMapper cabinetMapper;
    private final SpecialiteMapper specialiteMapper;
    private final UserMapper userMapper;

 
     // Récupère tous les cabinets actifs
    public List<CabinetResponse> getAllCabinets() {
        return cabinetRepository.findByStatus(Cabinet.Status.ACTIF)
                .stream()
                .map(cabinetMapper::toCabinetResponse)
                .toList();
    }

    public Page<CabinetResponse> getAllCabinets(Pageable pageable) {
        return cabinetRepository.findByStatus(Cabinet.Status.ACTIF, pageable)
                .map(cabinetMapper::toCabinetResponse);
    }

    
     //Récupère un cabinet par son ID
    public CabinetResponse getCabinetById(Long id) {
        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageErreur.CABINET_NOT_FOUND));
        return cabinetMapper.toCabinetResponse(cabinet);
    }

   
     //Récupère toutes les spécialités
    
    public List<SpecialiteResponse> getAllSpecialites() {
        return specialiteRepository.findAll()
                .stream()
                .map(specialiteMapper::toSpecialiteResponse)
                .toList();
    }

    
    // Récupère les spécialités d'un cabinet
    
    public List<SpecialiteResponse> getSpecialitesByCabinet(Long cabinetId) {
        if (!cabinetRepository.existsById(cabinetId)) {
            throw new EntityNotFoundException(MessageErreur.CABINET_NOT_FOUND);
        }
        
        return specialiteRepository.findByCabinetId(cabinetId)
                .stream()
                .map(specialiteMapper::toSpecialiteResponse)
                .toList();
    }


     // Récupère tous les médecins actifs avec filtres optionnels
    
    public List<MedecinResponse> getMedecins(Long specialiteId, Long cabinetId) {
        List<Utilisateur> medecins;
        
        if (specialiteId != null && cabinetId != null) {
            medecins = userRepository.findByRoleAndStatusAndSpecialiteIdAndCabinetId(
                    Role.MEDECIN, Status.ACTIF, specialiteId, cabinetId);
        } else if (specialiteId != null) {
            medecins = userRepository.findByRoleAndStatusAndSpecialiteId(
                    Role.MEDECIN, Status.ACTIF, specialiteId);
        } else if (cabinetId != null) {
            medecins = userRepository.findByRoleAndStatusAndCabinetId(
                    Role.MEDECIN, Status.ACTIF, cabinetId);
        } else {
            medecins = userRepository.findByRoleAndStatus(Role.MEDECIN, Status.ACTIF);
        }
        
        return medecins.stream()
                .map(userMapper::toMedecinResponse)
                .toList();
    }

    public Page<MedecinResponse> getMedecins(Long specialiteId, Long cabinetId, Pageable pageable) {
        Page<Utilisateur> medecins;

        if (specialiteId != null && cabinetId != null) {
            medecins = userRepository.findByRoleAndStatusAndSpecialiteIdAndCabinetId(
                    Role.MEDECIN, Status.ACTIF, specialiteId, cabinetId, pageable);
        } else if (specialiteId != null) {
            medecins = userRepository.findByRoleAndStatusAndSpecialiteId(
                    Role.MEDECIN, Status.ACTIF, specialiteId, pageable);
        } else if (cabinetId != null) {
            medecins = userRepository.findByRoleAndStatusAndCabinetId(
                    Role.MEDECIN, Status.ACTIF, cabinetId, pageable);
        } else {
            medecins = userRepository.findByRoleAndStatus(Role.MEDECIN, Status.ACTIF, pageable);
        }

        return medecins.map(userMapper::toMedecinResponse);
    }

    
     // Récupère un médecin par son ID
    
    public MedecinResponse getMedecinById(Long id) {
        Utilisateur medecin = userRepository.findById(id)
                .filter(u -> u.getRole() == Role.MEDECIN && u.getStatus() == Status.ACTIF)
                .orElseThrow(() -> new EntityNotFoundException(MessageErreur.MEDECIN_NOT_FOUND));
        return userMapper.toMedecinResponse(medecin);
    }
}

package com.medibook.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.medibook.common.security.SecurityService;
import com.medibook.common.storage.CloudinaryStorageService;
import com.medibook.user.dto.ProfileResponse;
import com.medibook.user.dto.UserRequest;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SecurityService securityService;
    private final CloudinaryStorageService storageService;

    @Transactional(readOnly = true)
    public ProfileResponse getProfile() {
        Utilisateur user = securityService.getUtilisateurConnecte();
        return toProfileResponse(user);
    }

    @Transactional
    public ProfileResponse updateProfile(UserRequest request) {
        Utilisateur user = securityService.getUtilisateurConnecte();
        if (request.prenom() != null) user.setPrenom(request.prenom());
        if (request.nom() != null) user.setNom(request.nom());
        if (request.telephone() != null) user.setTelephone(request.telephone());
        user = userRepository.save(user);
        return toProfileResponse(user);
    }

    @Transactional
    public ProfileResponse updateProfilePhoto(MultipartFile photo) {
        Utilisateur user = securityService.getUtilisateurConnecte();
        String folder = "medibook/patients/" + user.getId();
        String url = storageService.uploadFile(photo, folder);
        if (url != null) {
            user.setPhoto(url);
            user = userRepository.save(user);
        }
        return toProfileResponse(user);
    }

    private ProfileResponse toProfileResponse(Utilisateur user) {
        return ProfileResponse.builder()
                .id(user.getId())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .photo(user.getPhoto())
                .role(user.getRole().name())
                .build();
    }
}

package com.medibook.common.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * Service générique pour l'upload d'images/media
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MediaUploadService {

    private final StorageService storageService;

    /**
     * Upload une image de manière asynchrone
     * 
     * @param file Fichier à uploader
     * @param folder Dossier de destination (ex: "medibook/cabinets/12")
     * @return URL du fichier uploadé ou null en cas d'erreur
     */
    @Async
    public String uploadImageAsync(MultipartFile file, String folder) {
        try {
            // Générer un nom unique avec UUID
            String uniqueFileName = UUID.randomUUID().toString();

            String url = storageService.uploadFile(
                    file.getBytes(),
                    uniqueFileName,
                    folder
            );

            log.info("Image uploadée avec succès dans {}: {}", folder, url);
            return url;

        } catch (Exception e) {
            log.error("Erreur upload image dans {}: {}", folder, e.getMessage(), e);
            return null;
        }
    }

    /**
     * Upload une image de manière asynchrone avec callback
     * Le callback est exécuté après l'upload avec l'URL résultat
     * 
     * @param file Fichier à uploader
     * @param folder Dossier de destination
     * @param onComplete Callback appelé avec l'URL uploadée (peut être null si échec)
     */
    @Async
    public void uploadImageAsync(MultipartFile file, String folder, Consumer<String> onComplete) {
        try {
            String uniqueFileName = UUID.randomUUID().toString();

            String url = storageService.uploadFile(
                    file.getBytes(),
                    uniqueFileName,
                    folder
            );

            log.info("Image uploadée avec succès dans {}: {}", folder, url);
            
            if (onComplete != null) {
                onComplete.accept(url);
            }

        } catch (Exception e) {
            log.error("Erreur upload image dans {}: {}", folder, e.getMessage(), e);
            if (onComplete != null) {
                onComplete.accept(null);
            }
        }
    }

    /**
     * Upload une image de manière synchrone
     * 
     * @param file Fichier à uploader
     * @param folder Dossier de destination
     * @return URL du fichier uploadé
     */
    public String uploadImage(MultipartFile file, String folder) {
        try {
            String uniqueFileName = UUID.randomUUID().toString();

            return storageService.uploadFile(
                    file.getBytes(),
                    uniqueFileName,
                    folder
            );

        } catch (Exception e) {
            log.error("Erreur upload image dans {}: {}", folder, e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'upload de l'image", e);
        }
    }

    /**
     * Supprime une image du stockage
     * 
     * @param imageUrl URL de l'image à supprimer
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return;
        }

        try {
            String publicId = storageService.extractPublicId(imageUrl);
            if (publicId != null) {
                storageService.deleteFile(publicId);
                log.info("Image supprimée: {}", publicId);
            }
        } catch (Exception e) {
            log.error("Erreur suppression image: {}", e.getMessage(), e);
        }
    }

    /**
     * Supprime une image de manière asynchrone
     * 
     * @param imageUrl URL de l'image à supprimer
     */
    @Async
    public void deleteImageAsync(String imageUrl) {
        deleteImage(imageUrl);
    }
}

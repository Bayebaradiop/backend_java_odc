package com.medibook.common.storage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * Implémentation Cloudinary du service de stockage
 */
@Service
@Slf4j
public class CloudinaryStorageService implements StorageService {

    private final Cloudinary cloudinary;
    private final boolean enabled;

    public CloudinaryStorageService(
            @Value("${cloudinary.cloud-name:}") String cloudName,
            @Value("${cloudinary.api-key:}") String apiKey,
            @Value("${cloudinary.api-secret:}") String apiSecret) {
        
        // Vérifier si les identifiants sont configurés
        if (cloudName == null || cloudName.isEmpty() || cloudName.equals("demo")) {
            this.enabled = false;
            this.cloudinary = null;
            log.warn("Cloudinary n'est pas configuré. L'upload de fichiers est désactivé.");
        } else {
            this.enabled = true;
            this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret
            ));
            log.info("Cloudinary configuré avec le cloud: {}", cloudName);
        }
    }

    @Override
    public String uploadFile(byte[] fileBytes, String fileName, String folder) {
        if (!enabled) {
            log.warn("Cloudinary désactivé. Upload ignoré.");
            return null;
        }
        
        try {
            Map<String, Object> params = Map.of(
                    "folder", folder,
                    "resource_type", "image",
                    "public_id", fileName
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> result = cloudinary.uploader().upload(fileBytes, params);
            String url = (String) result.get("secure_url");
            log.info("Fichier uploadé vers Cloudinary: {}", url);
            return url;
        } catch (IOException e) {
            log.error("Erreur lors de l'upload vers Cloudinary: {}", e.getMessage());
            return null; // Retourne null au lieu de lever une exception
        }
    }

    /**
     * Upload depuis MultipartFile
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            return uploadFile(file.getBytes(), file.getOriginalFilename(), folder);
        } catch (IOException e) {
            log.error("Erreur lors de la lecture du fichier: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteFile(String publicId) {
        if (!enabled || publicId == null) {
            return;
        }
        
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Fichier supprimé de Cloudinary: {}", publicId);
        } catch (IOException e) {
            log.error("Erreur lors de la suppression de Cloudinary: {}", e.getMessage());
        }
    }

    @Override
    public String extractPublicId(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }
        // Extraire le public ID de l'URL Cloudinary
        try {
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1) {
                return null;
            }
            String path = imageUrl.substring(uploadIndex + 8);
            // Enlever l'extension
            if (path.contains(".")) {
                path = path.substring(0, path.lastIndexOf("."));
            }
            return path;
        } catch (Exception e) {
            log.warn("Impossible d'extraire le public ID de l'URL: {}", imageUrl);
            return null;
        }
    }
}

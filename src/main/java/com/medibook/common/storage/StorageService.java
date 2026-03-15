package com.medibook.common.storage;

/**
 * Interface abstraite pour le stockage de fichiers.
 * Permet de changer de fournisseur de stockage facilement (Cloudinary, AWS S3, etc.)
 */
public interface StorageService {

    /**
     * Télécharge un fichier et retourne l'URL publique
     * @param fileBytes Les bytes du fichier
     * @param fileName Le nom original du fichier
     * @param folder Le dossier de destination (ex: "cabinets/logos")
     * @return L'URL publique du fichier téléchargé
     */
    String uploadFile(byte[] fileBytes, String fileName, String folder);

    /**
     * Supprime un fichier du stockage
     * @param publicId L'identifiant public du fichier à supprimer
     */
    void deleteFile(String publicId);

    /**
     * Extrait le public ID d'une URL Cloudinary
     * @param imageUrl L'URL de l'image
     * @return Le public ID
     */
    String extractPublicId(String imageUrl);
}

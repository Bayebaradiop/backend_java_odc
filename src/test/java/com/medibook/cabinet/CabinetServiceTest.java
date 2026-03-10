/*
package com.medibook.cabinet;

import com.medibook.cabinet.dto.CabinetCreateDTO;
import com.medibook.cabinet.dto.CabinetResponseDTO;
import com.medibook.cabinet.entity.Cabinet;
import com.medibook.cabinet.mapper.CabinetMapper;
import com.medibook.cabinet.message.MessageErreur;
import com.medibook.cabinet.repository.CabinetRepository;
import com.medibook.cabinet.service.CabinetService;
import com.medibook.common.enums.Role;
import com.medibook.common.exception.BusinessException;
import com.medibook.common.exception.ResourceNotFoundException;
import com.medibook.common.exception.UnauthorizedException;
import com.medibook.common.storage.StorageService;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

*/
/**
 * Tests unitaires pour CabinetService
 *//*

@ExtendWith(MockitoExtension.class)
class CabinetServiceTest {

    @Mock
    private CabinetRepository cabinetRepository;

    @Mock
    private CabinetMapper cabinetMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private MultipartFile logoFile;

    @InjectMocks
    private CabinetService cabinetService;

    private Utilisateur superAdmin;
    private Utilisateur admin;
    private Utilisateur medecin;
    private Cabinet cabinet;
    private CabinetCreateDTO cabinetCreateDTO;

    @BeforeEach
    void setUp() {
        // Créer un Super Admin
        superAdmin = Utilisateur.builder()
                .id(1L)
                .prenom("Super")
                .nom("Admin")
                .email("superadmin@medibook.com")
                .role(Role.SUPER_ADMIN)
                .build();

        // Créer un Admin
        admin = Utilisateur.builder()
                .id(2L)
                .prenom("Admin")
                .nom("System")
                .email("admin@medibook.com")
                .role(Role.ADMIN)
                .build();

        // Créer un Médecin
        medecin = Utilisateur.builder()
                .id(3L)
                .prenom("Jean")
                .nom("Dupont")
                .email("jean.dupont@medibook.com")
                .role(Role.MEDECIN)
                .build();

        // Créer un cabinet
        cabinet = Cabinet.builder()
                .id(1L)
                .nom("Cabinet Medical")
                .adresse("123 Rue de la Santé")
                .telephone("+221 33 123 45 67")
                .email("contact@cabinet.com")
                .status(Cabinet.Status.ACTIF)
                .build();

        // Créer un DTO
        cabinetCreateDTO = new CabinetCreateDTO(
                "Nouveau Cabinet",
                "http://example.com/logo.png",
                "#007bff",
                "#ffffff",
                "456 Nouvelle Adresse",
                "+221 33 987 65 43",
                "nouveau@cabinet.com"
        );
    }

    // ==================== TESTS CRÉATION CABINET ====================

    @Test
    void createCabinet_Success_WhenUserIsSuperAdmin() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(superAdmin));
        when(cabinetRepository.existsByNom(cabinetCreateDTO.nom())).thenReturn(false);
        when(cabinetRepository.existsByEmail(cabinetCreateDTO.email())).thenReturn(false);
        when(cabinetMapper.toEntity(cabinetCreateDTO)).thenReturn(cabinet);
        when(cabinetRepository.save(any(Cabinet.class))).thenReturn(cabinet);
        when(cabinetMapper.toResponseDTO(cabinet)).thenReturn(
                new CabinetResponseDTO(1L, "Cabinet Medical", null, "#007bff", "#ffffff", 
                        "123 Rue", "+221 33 123 45 67", "contact@cabinet.com", "ACTIF")
        );

        // Act
        CabinetResponseDTO result = cabinetService.createCabinet(cabinetCreateDTO, null, 1L);

        // Assert
        assertNotNull(result);
        verify(cabinetRepository).save(any(Cabinet.class));
    }

    @Test
    void createCabinet_ThrowsUnauthorizedException_WhenUserIsNotSuperAdmin() {
        // Arrange - Admin essaie de créer un cabinet
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));

        // Act & Assert
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> cabinetService.createCabinet(cabinetCreateDTO, null, 2L)
        );

        assertEquals(MessageErreur.SEULEMENT_SUPER_ADMIN, exception.getMessage());
        verify(cabinetRepository, never()).save(any());
    }

    @Test
    void createCabinet_ThrowsUnauthorizedException_WhenUserIsMedecin() {
        // Arrange - Médecin essaie de créer un cabinet
        when(userRepository.findById(3L)).thenReturn(Optional.of(medecin));

        // Act & Assert
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> cabinetService.createCabinet(cabinetCreateDTO, null, 3L)
        );

        assertEquals(MessageErreur.SEULEMENT_SUPER_ADMIN, exception.getMessage());
        verify(cabinetRepository, never()).save(any());
    }

    @Test
    void createCabinet_ThrowsBusinessException_WhenCabinetNameAlreadyExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(superAdmin));
        when(cabinetRepository.existsByNom(cabinetCreateDTO.nom())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cabinetService.createCabinet(cabinetCreateDTO, null, 1L)
        );

        assertEquals(MessageErreur.CABINET_DEJA_EXISTANT, exception.getMessage());
        verify(cabinetRepository, never()).save(any());
    }

    @Test
    void createCabinet_ThrowsBusinessException_WhenEmailAlreadyExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(superAdmin));
        when(cabinetRepository.existsByNom(cabinetCreateDTO.nom())).thenReturn(false);
        when(cabinetRepository.existsByEmail(cabinetCreateDTO.email())).thenReturn(true);

        // Act & Assert
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> cabinetService.createCabinet(cabinetCreateDTO, null, 1L)
        );

        assertEquals(MessageErreur.EMAIL_DEJA_UTILISE, exception.getMessage());
        verify(cabinetRepository, never()).save(any());
    }

    @Test
    void createCabinet_ThrowsResourceNotFoundException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> cabinetService.createCabinet(cabinetCreateDTO, null, 999L)
        );
    }

    // ==================== TESTS RÉCUPÉRATION ====================

    @Test
    void getCabinetById_ReturnsCabinet_WhenExists() {
        // Arrange
        when(cabinetRepository.findById(1L)).thenReturn(Optional.of(cabinet));
        when(cabinetMapper.toResponseDTO(cabinet)).thenReturn(
                new CabinetResponseDTO(1L, "Cabinet Medical", null, "#007bff", "#ffffff", 
                        "123 Rue", "+221 33 123 45 67", "contact@cabinet.com", "ACTIF")
        );

        // Act
        CabinetResponseDTO result = cabinetService.getCabinetById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void getCabinetById_ThrowsException_WhenNotFound() {
        // Arrange
        when(cabinetRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                ResourceNotFoundException.class,
                () -> cabinetService.getCabinetById(999L)
        );
    }

    // ==================== TESTS MISE À JOUR ====================

    @Test
    void updateCabinet_Success_WhenUserIsSuperAdmin() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(superAdmin));
        when(cabinetRepository.findById(1L)).thenReturn(Optional.of(cabinet));
        when(cabinetRepository.existsByNom("Nouveau Cabinet")).thenReturn(false);
        when(cabinetRepository.existsByEmail("nouveau@cabinet.com")).thenReturn(false);
        when(cabinetRepository.save(any(Cabinet.class))).thenReturn(cabinet);
        when(cabinetMapper.toResponseDTO(cabinet)).thenReturn(
                new CabinetResponseDTO(1L, "Nouveau Cabinet", null, "#007bff", "#ffffff", 
                        "456 Nouvelle Adresse", "+221 33 987 65 43", "nouveau@cabinet.com", "ACTIF")
        );

        // Act
        CabinetResponseDTO result = cabinetService.updateCabinet(1L, cabinetCreateDTO, null, 1L);

        // Assert
        assertNotNull(result);
        verify(cabinetRepository).save(any(Cabinet.class));
    }

    @Test
    void updateCabinet_ThrowsUnauthorizedException_WhenUserIsNotSuperAdmin() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));

        // Act & Assert
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> cabinetService.updateCabinet(1L, cabinetCreateDTO, null, 2L)
        );

        assertEquals(MessageErreur.ACCES_INTERDIT, exception.getMessage());
        verify(cabinetRepository, never()).save(any());
    }

    // ==================== TESTS SUPPRESSION ====================

    @Test
    void deleteCabinet_Success_WhenUserIsSuperAdmin() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(superAdmin));
        when(cabinetRepository.findById(1L)).thenReturn(Optional.of(cabinet));
        doNothing().when(cabinetRepository).delete(cabinet);

        // Act
        cabinetService.deleteCabinet(1L, 1L);

        // Assert
        verify(cabinetRepository).delete(cabinet);
    }

    @Test
    void deleteCabinet_ThrowsUnauthorizedException_WhenUserIsNotSuperAdmin() {
        // Arrange
        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));

        // Act & Assert
        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> cabinetService.deleteCabinet(1L, 2L)
        );

        assertEquals(MessageErreur.ACCES_INTERDIT, exception.getMessage());
        verify(cabinetRepository, never()).delete(any());
    }

    // ==================== TESTS CHANGEMENT STATUT ====================

    @Test
    void toggleCabinetStatus_Success_WhenUserIsSuperAdmin() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(superAdmin));
        when(cabinetRepository.findById(1L)).thenReturn(Optional.of(cabinet));
        when(cabinetRepository.save(any(Cabinet.class))).thenReturn(cabinet);
        when(cabinetMapper.toResponseDTO(cabinet)).thenReturn(
                new CabinetResponseDTO(1L, "Cabinet Medical", null, "#007bff", "#ffffff", 
                        "123 Rue", "+221 33 123 45 67", "contact@cabinet.com", "INACTIF")
        );

        // Act
        CabinetResponseDTO result = cabinetService.toggleCabinetStatus(1L, 1L);

        // Assert
        assertNotNull(result);
        verify(cabinetRepository).save(cabinet);
    }
}
*/

package com.medibook.common.config.seeder;

import com.medibook.ExceptionsPlanning.repository.ExceptionsPlanningRepository;
import com.medibook.cabinet.repository.CabinetRepository;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;
import com.medibook.creneau.repository.CreneauRepository;
import com.medibook.planning.repository.PlanningRepository;
import com.medibook.rendezvous.repository.RendezVousRepository;
import com.medibook.specialite.repository.SpecialiteRepository;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSeeder implements Seeder {

    private final UserRepository userRepository;
    private final CabinetRepository cabinetRepository;
    private final SpecialiteRepository specialiteRepository;
    private final RendezVousRepository rendezVousRepository;
    private final CreneauRepository creneauRepository;
    private final PlanningRepository planningRepository;
    private final ExceptionsPlanningRepository exceptionsPlanningRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "123456";
    private static final String SUPER_ADMIN_EMAIL = "superadmin@medibook.com";

    @Override
    public String getName() {
        return "UserSeeder";
    }

    @Override
    public int getOrder() {
        return 1; // S'exécute en premier pour purger et créer le Super Admin
    }

    @Override
    public boolean shouldRun() {
        // Exécuter la purge si d'autres données existent ou si le super admin est manquant
        boolean superAdminMissing = userRepository.findByEmail(SUPER_ADMIN_EMAIL).isEmpty();
        boolean hasOtherData = userRepository.count() > (superAdminMissing ? 0 : 1)
                || cabinetRepository.count() > 0
                || specialiteRepository.count() > 0;
        return superAdminMissing || hasOtherData;
    }

    @Override
    @Transactional
    public void run() {
        log.info("🧹 Purge complète des données de la base de données...");

        try {
            rendezVousRepository.deleteAll();
            creneauRepository.deleteAll();
            planningRepository.deleteAll();
            exceptionsPlanningRepository.deleteAll();
            
            // Supprimer tous les utilisateurs sauf SUPER_ADMIN
            List<Utilisateur> nonSuperAdmins = userRepository.findAll().stream()
                    .filter(u -> u.getRole() != Role.SUPER_ADMIN)
                    .toList();
            if (!nonSuperAdmins.isEmpty()) {
                userRepository.deleteAll(nonSuperAdmins);
            }

            specialiteRepository.deleteAll();
            cabinetRepository.deleteAll();

            log.info("✅ Purge terminée : Rendez-vous, Créneaux, Plannings, Spécialités, Cabinets et comptes secondaires supprimés.");
        } catch (Exception e) {
            log.error("⚠️ Erreur lors de la purge complète des données: {}", e.getMessage());
        }

        // Créer le Super Admin s'il n'existe pas
        if (userRepository.findByEmail(SUPER_ADMIN_EMAIL).isEmpty()) {
            Utilisateur superAdmin = Utilisateur.builder()
                    .prenom("Super")
                    .nom("Admin")
                    .email(SUPER_ADMIN_EMAIL)
                    .telephone("+221330000000")
                    .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                    .role(Role.SUPER_ADMIN)
                    .status(Status.ACTIF)
                    .build();
            userRepository.save(superAdmin);
            log.info("   ✅ Super Admin créé avec succès: {}", SUPER_ADMIN_EMAIL);
        } else {
            log.info("   ℹ️ Super Admin déjà présent: {}", SUPER_ADMIN_EMAIL);
        }

        log.info("🎉 Base de données nettoyée avec succès - Seul le Super Admin est désormais présent !");
    }
}

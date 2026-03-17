package com.medibook.common.config.seeder;

import com.medibook.cabinet.entity.Cabinet;
import com.medibook.cabinet.repository.CabinetRepository;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.Status;
import com.medibook.specialite.entity.Specialite;
import com.medibook.specialite.repository.SpecialiteRepository;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSeeder implements Seeder {

    private final UserRepository userRepository;
    private final CabinetRepository cabinetRepository;
    private final SpecialiteRepository specialiteRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public String getName() {
        return "UserSeeder";
    }

    @Override
    public int getOrder() {
        return 3;  // S'exécute après SpecialiteSeeder
    }

    @Override
    public boolean shouldRun() {
        return userRepository.count() == 0;
    }

    @Override
    public void run() {
        log.info("🌱 Exécution de UserSeeder...");

        // Récupérer les cabinets et spécialités existants
        List<Cabinet> cabinets = cabinetRepository.findAll();
        List<Specialite> specialites = specialiteRepository.findAll();

        if (cabinets.isEmpty()) {
            log.warn("   ⚠️ Aucun cabinet trouvé, utilisateurs non créés");
            return;
        }

        Cabinet cabinetPrincipal = cabinets.get(0);
        Specialite medecineGenerale = specialites.stream()
                .filter(s -> s.getNom().equals("Médecine Générale"))
                .findFirst()
                .orElse(specialites.isEmpty() ? null : specialites.get(0));

        // Créer le Super Admin (sans cabinet)
        Utilisateur superAdmin = Utilisateur.builder()
                .prenom("Super")
                .nom("Admin")
                .email("superadmin@medibook.com")
                .telephone("+221330000000")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .role(Role.SUPER_ADMIN)
                .status(Status.ACTIF)
                .build();
        userRepository.save(superAdmin);
        log.info("   ✅ Super Admin créé: superadmin@medibook.com (sans cabinet)");

        // Créer un Admin (lié au cabinet principal)
        Utilisateur admin = Utilisateur.builder()
                .prenom("Admin")
                .nom("System")
                .email("admin@medibook.com")
                .telephone("+221330000001")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .role(Role.ADMIN)
                .status(Status.ACTIF)
                .cabinet(cabinetPrincipal)
                .build();
        userRepository.save(admin);
        log.info("   ✅ Admin créé: admin@medibook.com - Cabinet: {}", cabinetPrincipal.getNom());

        // Créer un Médecin (lié au cabinet et à une spécialité)
        Utilisateur medecin = Utilisateur.builder()
                .prenom("Jean")
                .nom("Dupont")
                .email("jean.dupont@medibook.com")
                .telephone("+221330000002")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .role(Role.MEDECIN)
                .status(Status.ACTIF)
                .cabinet(cabinetPrincipal)
                .specialite(medecineGenerale)
                .build();
        userRepository.save(medecin);
        log.info("   ✅ Médecin créé: jean.dupont@medibook.com - Cabinet: {}, Spécialité: {}", 
                cabinetPrincipal.getNom(), medecineGenerale != null ? medecineGenerale.getNom() : "N/A");

        // Créer une Secrétaire (liée au cabinet + spécialité pour pouvoir voir les médecins)
        Utilisateur secretaire = Utilisateur.builder()
                .prenom("Marie")
                .nom("Sarr")
                .email("marie.sarr@medibook.com")
                .telephone("+221330000003")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .role(Role.SECRETAIRE)
                .status(Status.ACTIF)
                .cabinet(cabinetPrincipal)
                .specialite(medecineGenerale)
                .build();
        userRepository.save(secretaire);
        log.info("   ✅ Secrétaire créée: marie.sarr@medibook.com - Cabinet: {}, Spécialité: {}", 
                cabinetPrincipal.getNom(), medecineGenerale != null ? medecineGenerale.getNom() : "N/A");

        // Créer un 2ème Médecin (Cardiologie - cabinet principal)
        Specialite cardiologie = specialites.stream()
                .filter(s -> s.getNom().equals("Cardiologie"))
                .findFirst()
                .orElse(medecineGenerale);

        Utilisateur medecin2 = Utilisateur.builder()
                .prenom("Amadou")
                .nom("Ba")
                .email("amadou.ba@medibook.com")
                .telephone("+221330000004")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .role(Role.MEDECIN)
                .status(Status.ACTIF)
                .cabinet(cabinetPrincipal)
                .specialite(cardiologie)
                .build();
        userRepository.save(medecin2);
        log.info("   ✅ Médecin 2 créé: amadou.ba@medibook.com - Cabinet: {}, Spécialité: {}", 
                cabinetPrincipal.getNom(), cardiologie != null ? cardiologie.getNom() : "N/A");

        // Créer un 2ème Admin (cabinet Sud) si 2ème cabinet existe
        if (cabinets.size() > 1) {
            Cabinet cabinetSud = cabinets.get(1);

            Utilisateur adminSud = Utilisateur.builder()
                    .prenom("Ousmane")
                    .nom("Ndiaye")
                    .email("admin.sud@medibook.com")
                    .telephone("+221330000005")
                    .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                    .role(Role.ADMIN)
                    .status(Status.ACTIF)
                    .cabinet(cabinetSud)
                    .build();
            userRepository.save(adminSud);
            log.info("   ✅ Admin Sud créé: admin.sud@medibook.com - Cabinet: {}", cabinetSud.getNom());

            // Médecin pour cabinet Sud
            Specialite ophtalmologie = specialites.stream()
                    .filter(s -> s.getNom().equals("Ophtalmologie"))
                    .findFirst()
                    .orElse(null);

            if (ophtalmologie != null) {
                Utilisateur medecinSud = Utilisateur.builder()
                        .prenom("Ibrahima")
                        .nom("Diop")
                        .email("ibrahima.diop@medibook.com")
                        .telephone("+221330000006")
                        .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                        .role(Role.MEDECIN)
                        .status(Status.ACTIF)
                        .cabinet(cabinetSud)
                        .specialite(ophtalmologie)
                        .build();
                userRepository.save(medecinSud);
                log.info("   ✅ Médecin Sud créé: ibrahima.diop@medibook.com");
            }

            // Secrétaire pour cabinet Sud
            Utilisateur secretaireSud = Utilisateur.builder()
                    .prenom("Aissatou")
                    .nom("Fall")
                    .email("aissatou.fall@medibook.com")
                    .telephone("+221330000007")
                    .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                    .role(Role.SECRETAIRE)
                    .status(Status.ACTIF)
                    .cabinet(cabinetSud)
                    .specialite(ophtalmologie)
                    .build();
            userRepository.save(secretaireSud);
            log.info("   ✅ Secrétaire Sud créée: aissatou.fall@medibook.com");
        }

        // Créer un Patient (sans cabinet)
        Utilisateur patient = Utilisateur.builder()
                .prenom("Fatou")
                .nom("Sall")
                .email("fatou.sall@email.com")
                .telephone("+221770000001")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .role(Role.PATIENT)
                .status(Status.ACTIF)
                .build();
        userRepository.save(patient);
        log.info("   ✅ Patient créé: fatou.sall@email.com");

        // Créer un deuxième Patient
        Utilisateur patient2 = Utilisateur.builder()
                .prenom("Aliou")
                .nom("Diallo")
                .email("aliou.diallo@email.com")
                .telephone("+221770000002")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .role(Role.PATIENT)
                .status(Status.ACTIF)
                .build();
        userRepository.save(patient2);
        log.info("   ✅ Patient créé: aliou.diallo@email.com");

        log.info("✅ UserSeeder terminé - {} utilisateurs créés", userRepository.count());
    }
}

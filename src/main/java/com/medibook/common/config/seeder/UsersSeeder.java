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
public class UsersSeeder implements Seeder {

    private final UserRepository userRepository;
    private final CabinetRepository cabinetRepository;
    private final SpecialiteRepository specialiteRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String DEFAULT_PASSWORD = "123456";

    @Override
    public String getName() {
        return "UsersSeeder";
    }

    @Override
    public int getOrder() {
        return 4; // Exécution après CabinetSeeder et SpecialiteSeeder
    }

    @Override
    public boolean shouldRun() {
        // Exécuter si uniquement SuperAdmin est présent (count <= 1)
        return userRepository.count() <= 1;
    }

    @Override
    public void run() {
        log.info("🌱 Exécution de UsersSeeder avec photos pour chaque utilisateur...");

        List<Cabinet> cabinets = cabinetRepository.findAll();
        List<Specialite> specialites = specialiteRepository.findAll();

        if (cabinets.isEmpty() || specialites.isEmpty()) {
            log.warn("   ⚠️ Cabinets ou spécialités manquants. Création utilisateurs ignorée.");
            return;
        }

        Cabinet cabinet1 = cabinets.get(0);
        Cabinet cabinet2 = cabinets.size() > 1 ? cabinets.get(1) : cabinet1;

        // 1. ADMINS DE CABINETS
        Utilisateur admin1 = Utilisateur.builder()
                .prenom("Fatou")
                .nom("Sow")
                .email("admin.medibook@medibook.com")
                .telephone("+221770000001")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .photo("https://images.unsplash.com/photo-1573496359142-b8d87734a5a2?w=400&auto=format&fit=crop&q=80")
                .role(Role.ADMIN)
                .status(Status.ACTIF)
                .cabinet(cabinet1)
                .build();
        userRepository.save(admin1);

        Utilisateur admin2 = Utilisateur.builder()
                .prenom("Ousmane")
                .nom("Ba")
                .email("admin.sud@medibook.com")
                .telephone("+221770000002")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .photo("https://images.unsplash.com/photo-1560250097-0b93528c311a?w=400&auto=format&fit=crop&q=80")
                .role(Role.ADMIN)
                .status(Status.ACTIF)
                .cabinet(cabinet2)
                .build();
        userRepository.save(admin2);

        // 2. MÉDECINS
        Specialite medecineGen = findSpecialite(specialites, "Médecine Générale");
        Specialite cardio = findSpecialite(specialites, "Cardiologie");
        Specialite pediatrie = findSpecialite(specialites, "Pédiatrie");
        Specialite dermato = findSpecialite(specialites, "Dermatologie");
        Specialite gyneco = findSpecialite(specialites, "Gynécologie");
        Specialite ophtalmo = findSpecialite(specialites, "Ophtalmologie");

        Utilisateur drDiallo = Utilisateur.builder()
                .prenom("Amadou")
                .nom("Diallo")
                .email("dr.diallo@medibook.com")
                .telephone("+221771112233")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .photo("https://images.unsplash.com/photo-1622253692010-333f2da6031d?w=400&auto=format&fit=crop&q=80")
                .role(Role.MEDECIN)
                .status(Status.ACTIF)
                .cabinet(cabinet1)
                .specialite(medecineGen)
                .build();
        userRepository.save(drDiallo);

        Utilisateur drNdiaye = Utilisateur.builder()
                .prenom("Aminata")
                .nom("Ndiaye")
                .email("dr.ndiaye@medibook.com")
                .telephone("+221772223344")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .photo("https://images.unsplash.com/photo-1594824813566-78a9c3725b84?w=400&auto=format&fit=crop&q=80")
                .role(Role.MEDECIN)
                .status(Status.ACTIF)
                .cabinet(cabinet1)
                .specialite(cardio)
                .build();
        userRepository.save(drNdiaye);

        Utilisateur drSow = Utilisateur.builder()
                .prenom("Cheikh")
                .nom("Sow")
                .email("dr.sow@medibook.com")
                .telephone("+221773334455")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .photo("https://images.unsplash.com/photo-1537368910025-700350fe46c7?w=400&auto=format&fit=crop&q=80")
                .role(Role.MEDECIN)
                .status(Status.ACTIF)
                .cabinet(cabinet1)
                .specialite(pediatrie)
                .build();
        userRepository.save(drSow);

        Utilisateur drSy = Utilisateur.builder()
                .prenom("Fatou")
                .nom("Sy")
                .email("dr.sy@medibook.com")
                .telephone("+221774445566")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .photo("https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=400&auto=format&fit=crop&q=80")
                .role(Role.MEDECIN)
                .status(Status.ACTIF)
                .cabinet(cabinet1)
                .specialite(dermato)
                .build();
        userRepository.save(drSy);

        Utilisateur drKane = Utilisateur.builder()
                .prenom("Ousmane")
                .nom("Kane")
                .email("dr.kane@medibook.com")
                .telephone("+221775556677")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .photo("https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=400&auto=format&fit=crop&q=80")
                .role(Role.MEDECIN)
                .status(Status.ACTIF)
                .cabinet(cabinet1)
                .specialite(gyneco)
                .build();
        userRepository.save(drKane);

        Utilisateur drBa = Utilisateur.builder()
                .prenom("Mariama")
                .nom("Ba")
                .email("dr.ba@medibook.com")
                .telephone("+221776667788")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .photo("https://images.unsplash.com/photo-1582750433449-648ed127bb54?w=400&auto=format&fit=crop&q=80")
                .role(Role.MEDECIN)
                .status(Status.ACTIF)
                .cabinet(cabinet2)
                .specialite(ophtalmo)
                .build();
        userRepository.save(drBa);

        // 3. PATIENTS
        Utilisateur patient1 = Utilisateur.builder()
                .prenom("Moussa")
                .nom("Diop")
                .email("moussa.diop@gmail.com")
                .telephone("+221781001122")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .photo("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&auto=format&fit=crop&q=80")
                .role(Role.PATIENT)
                .status(Status.ACTIF)
                .build();
        userRepository.save(patient1);

        Utilisateur patient2 = Utilisateur.builder()
                .prenom("Awa")
                .nom("Fall")
                .email("awa.fall@yahoo.fr")
                .telephone("+221782002233")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .photo("https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=400&auto=format&fit=crop&q=80")
                .role(Role.PATIENT)
                .status(Status.ACTIF)
                .build();
        userRepository.save(patient2);

        Utilisateur patient3 = Utilisateur.builder()
                .prenom("Ibrahima")
                .nom("Gaye")
                .email("ibrahima.gaye@hotmail.com")
                .telephone("+221783003344")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .photo("https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=400&auto=format&fit=crop&q=80")
                .role(Role.PATIENT)
                .status(Status.ACTIF)
                .build();
        userRepository.save(patient3);

        Utilisateur patient4 = Utilisateur.builder()
                .prenom("Khadija")
                .nom("Thiam")
                .email("khadija.thiam@gmail.com")
                .telephone("+221784004455")
                .motDePasse(passwordEncoder.encode(DEFAULT_PASSWORD))
                .photo("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=400&auto=format&fit=crop&q=80")
                .role(Role.PATIENT)
                .status(Status.ACTIF)
                .build();
        userRepository.save(patient4);

        log.info("✅ UsersSeeder terminé - {} utilisateurs créés avec leurs photos", userRepository.count());
    }

    private Specialite findSpecialite(List<Specialite> list, String nom) {
        return list.stream()
                .filter(s -> s.getNom().equalsIgnoreCase(nom))
                .findFirst()
                .orElse(list.isEmpty() ? null : list.get(0));
    }
}

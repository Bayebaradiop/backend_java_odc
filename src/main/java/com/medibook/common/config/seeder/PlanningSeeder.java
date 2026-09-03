package com.medibook.common.config.seeder;

import com.medibook.common.enums.JourSemaine;
import com.medibook.common.enums.Role;
import com.medibook.planning.entity.TemplateSemaine;
import com.medibook.planning.repository.PlanningRepository;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PlanningSeeder implements Seeder {

    private final PlanningRepository planningRepository;
    private final UserRepository userRepository;

    @Override
    public String getName() {
        return "PlanningSeeder";
    }

    @Override
    public int getOrder() {
        return 5;  // S'exécute après UsersSeeder (ordre 4)
    }

    @Override
    public boolean shouldRun() {
        return planningRepository.count() == 0;
    }

    @Override
    public void run() {
        log.info("🌱 Exécution de PlanningSeeder...");

        List<Utilisateur> medecins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.MEDECIN)
                .toList();

        if (medecins.isEmpty()) {
            log.warn("   ⚠️ Aucun médecin trouvé, plannings ignorés");
            return;
        }

        for (Utilisateur medecin : medecins) {
            log.info("   📅 Création du planning pour le Dr. {} {}", medecin.getPrenom(), medecin.getNom());
            
            for (JourSemaine jour : List.of(JourSemaine.LUNDI, JourSemaine.MARDI, JourSemaine.MERCREDI, JourSemaine.JEUDI, JourSemaine.VENDREDI)) {
                // Matin: 08:00 - 12:00
                planningRepository.save(TemplateSemaine.builder()
                        .medecin(medecin)
                        .jourSemaine(jour)
                        .heureDebut(LocalTime.of(8, 0))
                        .heureFin(LocalTime.of(12, 0))
                        .dureeCreneau(30)
                        .build());

                // Après-midi: 14:00 - 18:00
                planningRepository.save(TemplateSemaine.builder()
                        .medecin(medecin)
                        .jourSemaine(jour)
                        .heureDebut(LocalTime.of(14, 0))
                        .heureFin(LocalTime.of(18, 0))
                        .dureeCreneau(30)
                        .build());
            }
        }

        log.info("✅ PlanningSeeder terminé - {} plannings créés", planningRepository.count());
    }
}

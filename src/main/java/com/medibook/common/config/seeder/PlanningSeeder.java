// package com.medibook.common.config.seeder;

// import com.medibook.common.enums.JourSemaine;
// import com.medibook.common.enums.Role;
// import com.medibook.planning.entity.TemplateSemaine;
// import com.medibook.planning.repository.PlanningRepository;
// import com.medibook.user.entity.Utilisateur;
// import com.medibook.user.repository.UserRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Component;

// import java.time.LocalTime;
// import java.util.List;

// @Component
// @RequiredArgsConstructor
// @Slf4j
// public class PlanningSeeder implements Seeder {

//     private final PlanningRepository planningRepository;
//     private final UserRepository userRepository;

//     @Override
//     public String getName() {
//         return "PlanningSeeder";
//     }

//     @Override
//     public int getOrder() {
//         return 4;  // S'exécute après UserSeeder
//     }

//     @Override
//     public boolean shouldRun() {
//         return planningRepository.count() == 0;
//     }

//     @Override
//     public void run() {
//         log.info("🌱 Exécution de PlanningSeeder...");

//         // Récupérer les médecins
//         List<Utilisateur> medecins = userRepository.findAll().stream()
//                 .filter(u -> u.getRole() == Role.MEDECIN)
//                 .toList();

//         if (medecins.isEmpty()) {
//             log.warn("   ⚠️ Aucun médecin trouvé, plannings ignorés");
//             return;
//         }

//         for (Utilisateur medecin : medecins) {
//             log.info("   📅 Création du planning pour leDr. {} {}", medecin.getPrenom(), medecin.getNom());
            
//             // Planning du lundi au vendredi
//             // Matin: 08:00 - 12:00
//             // Après-midi: 14:00 - 18:00
//             // Créneau de 30 minutes
            
//             // Lundi matin
//             planningRepository.save(TemplateSemaine.builder()
//                     .medecin(medecin)
//                     .jourSemaine(JourSemaine.LUNDI)
//                     .heureDebut(LocalTime.of(8, 0))
//                     .heureFin(LocalTime.of(12, 0))
//                     .dureeCreneau(30)
//                     .build());
            
//             // Lundi après-midi
//             planningRepository.save(TemplateSemaine.builder()
//                     .medecin(medecin)
//                     .jourSemaine(JourSemaine.LUNDI)
//                     .heureDebut(LocalTime.of(14, 0))
//                     .heureFin(LocalTime.of(18, 0))
//                     .dureeCreneau(30)
//                     .build());

//             // Mardi matin
//             planningRepository.save(TemplateSemaine.builder()
//                     .medecin(medecin)
//                     .jourSemaine(JourSemaine.MARDI)
//                     .heureDebut(LocalTime.of(8, 0))
//                     .heureFin(LocalTime.of(12, 0))
//                     .dureeCreneau(30)
//                     .build());

//             // Mardi après-midi
//             planningRepository.save(TemplateSemaine.builder()
//                     .medecin(medecin)
//                     .jourSemaine(JourSemaine.MARDI)
//                     .heureDebut(LocalTime.of(14, 0))
//                     .heureFin(LocalTime.of(18, 0))
//                     .dureeCreneau(30)
//                     .build());

//             // Mercredi matin
//             planningRepository.save(TemplateSemaine.builder()
//                     .medecin(medecin)
//                     .jourSemaine(JourSemaine.MERCREDI)
//                     .heureDebut(LocalTime.of(8, 0))
//                     .heureFin(LocalTime.of(12, 0))
//                     .dureeCreneau(30)
//                     .build());

//             // Mercredi après-midi
//             planningRepository.save(TemplateSemaine.builder()
//                     .medecin(medecin)
//                     .jourSemaine(JourSemaine.MERCREDI)
//                     .heureDebut(LocalTime.of(14, 0))
//                     .heureFin(LocalTime.of(18, 0))
//                     .dureeCreneau(30)
//                     .build());

//             // Jeudi matin
//             planningRepository.save(TemplateSemaine.builder()
//                     .medecin(medecin)
//                     .jourSemaine(JourSemaine.JEUDI)
//                     .heureDebut(LocalTime.of(8, 0))
//                     .heureFin(LocalTime.of(12, 0))
//                     .dureeCreneau(30)
//                     .build());

//             // Jeudi après-midi
//             planningRepository.save(TemplateSemaine.builder()
//                     .medecin(medecin)
//                     .jourSemaine(JourSemaine.JEUDI)
//                     .heureDebut(LocalTime.of(14, 0))
//                     .heureFin(LocalTime.of(18, 0))
//                     .dureeCreneau(30)
//                     .build());

//             // Vendredi matin
//             planningRepository.save(TemplateSemaine.builder()
//                     .medecin(medecin)
//                     .jourSemaine(JourSemaine.VENDREDI)
//                     .heureDebut(LocalTime.of(8, 0))
//                     .heureFin(LocalTime.of(12, 0))
//                     .dureeCreneau(30)
//                     .build());

//             // Vendredi après-midi
//             planningRepository.save(TemplateSemaine.builder()
//                     .medecin(medecin)
//                     .jourSemaine(JourSemaine.VENDREDI)
//                     .heureDebut(LocalTime.of(14, 0))
//                     .heureFin(LocalTime.of(17, 0))
//                     .dureeCreneau(30)
//                     .build());

//             log.info("      ✅ {} créneaux créés pour le Dr. {}", 10, medecin.getNom());
//         }

//         log.info("✅ PlanningSeeder terminé - {} plannings créés", planningRepository.count());
//     }
// }

package com.medibook.common.config.seeder;

import com.medibook.common.enums.JourSemaine;
import com.medibook.common.enums.Role;
import com.medibook.creneau.entity.Creneau;
import com.medibook.creneau.repository.CreneauRepository;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreneauSeeder implements Seeder {

    private final CreneauRepository creneauRepository;
    private final UserRepository userRepository;

    @Override
    public String getName() {
        return "CreneauSeeder";
    }

    @Override
    public int getOrder() {
        return 6;  // Exécution après PlanningSeeder
    }

    @Override
    public boolean shouldRun() {
        return creneauRepository.count() == 0;
    }

    @Override
    public void run() {
        log.info("🌱 Exécution de CreneauSeeder...");

        List<Utilisateur> medecins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.MEDECIN)
                .toList();

        if (medecins.isEmpty()) {
            log.warn("   ⚠️ Aucun médecin trouvé, créneaux ignorés");
            return;
        }

        LocalDate today = LocalDate.now();
        
        for (Utilisateur medecin : medecins) {
            int creneauCount = 0;
            
            for (int dayOffset = 0; dayOffset < 14; dayOffset++) {
                LocalDate date = today.plusDays(dayOffset);
                JourSemaine jourSemaine = getJourSemaine(date.getDayOfWeek().name());
                
                if (jourSemaine == JourSemaine.DIMANCHE) continue;
                
                LocalTime[] debutSlots;
                if (jourSemaine == JourSemaine.SAMEDI) {
                    debutSlots = new LocalTime[]{
                        LocalTime.of(8, 0), LocalTime.of(8, 30),
                        LocalTime.of(9, 0), LocalTime.of(9, 30),
                        LocalTime.of(10, 0), LocalTime.of(10, 30),
                        LocalTime.of(11, 0), LocalTime.of(11, 30)
                    };
                } else {
                    debutSlots = new LocalTime[]{
                        LocalTime.of(8, 0), LocalTime.of(8, 30),
                        LocalTime.of(9, 0), LocalTime.of(9, 30),
                        LocalTime.of(10, 0), LocalTime.of(10, 30),
                        LocalTime.of(11, 0), LocalTime.of(11, 30),
                        LocalTime.of(14, 0), LocalTime.of(14, 30),
                        LocalTime.of(15, 0), LocalTime.of(15, 30),
                        LocalTime.of(16, 0), LocalTime.of(16, 30),
                        LocalTime.of(17, 0), LocalTime.of(17, 30)
                    };
                }
                
                for (LocalTime debut : debutSlots) {
                    LocalTime fin = debut.plusMinutes(30);
                    
                    Creneau creneau = Creneau.builder()
                            .medecin(medecin)
                            .date(date)
                            .heureDebut(debut)
                            .heureFin(fin)
                            .disponible(true)
                            .build();
                    
                    creneauRepository.save(creneau);
                    creneauCount++;
                }
            }
            
            log.info("      ✅ {} créneaux générés pour le Dr. {}", creneauCount, medecin.getNom());
        }

        log.info("✅ CreneauSeeder terminé - {} créneaux créés au total", creneauRepository.count());
    }
    
    private JourSemaine getJourSemaine(String dayOfWeek) {
        return switch (dayOfWeek) {
            case "MONDAY" -> JourSemaine.LUNDI;
            case "TUESDAY" -> JourSemaine.MARDI;
            case "WEDNESDAY" -> JourSemaine.MERCREDI;
            case "THURSDAY" -> JourSemaine.JEUDI;
            case "FRIDAY" -> JourSemaine.VENDREDI;
            case "SATURDAY" -> JourSemaine.SAMEDI;
            case "SUNDAY" -> JourSemaine.DIMANCHE;
            default -> null;
        };
    }
}

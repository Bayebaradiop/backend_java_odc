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
        return 4;  // S'exécute avant RendezVousSeeder (maintenant order 6)
    }

    @Override
    public boolean shouldRun() {
        return creneauRepository.count() == 0;
    }

    @Override
    public void run() {
        log.info("🌱 Exécution de CreneauSeeder...");

        // Récupérer les médecins
        List<Utilisateur> medecins = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.MEDECIN)
                .toList();

        if (medecins.isEmpty()) {
            log.warn("   ⚠️ Aucun médecin trouvé, créneaux ignorés");
            return;
        }

        // Générer des créneaux pour les 2 prochaines semaines
        LocalDate today = LocalDate.now();
        
        for (Utilisateur medecin : medecins) {
            log.info("   📅 Génération des créneaux pour leDr. {} {}", medecin.getPrenom(), medecin.getNom());
            
            int creneauCount = 0;
            
            // Générer pour les 14 prochains jours
            for (int dayOffset = 0; dayOffset < 14; dayOffset++) {
                LocalDate date = today.plusDays(dayOffset);
                JourSemaine jourSemaine = getJourSemaine(date.getDayOfWeek().name());
                
                // Skip dimanche
                if (jourSemaine == JourSemaine.DIMANCHE) continue;
                
                // Définir les horaires selon le jour
                LocalTime[] debutSlots;
                if (jourSemaine == JourSemaine.SAMEDI) {
                    // Samedi: 08:00 - 13:00 seulement
                    debutSlots = new LocalTime[]{
                        LocalTime.of(8, 0), LocalTime.of(8, 30),
                        LocalTime.of(9, 0), LocalTime.of(9, 30),
                        LocalTime.of(10, 0), LocalTime.of(10, 30),
                        LocalTime.of(11, 0), LocalTime.of(11, 30),
                        LocalTime.of(12, 0), LocalTime.of(12, 30)
                    };
                } else {
                    // Lundi à vendredi: matin + après-midi
                    // Matin: 08:00 - 12:00 (8 créneaux de 30min)
                    // Après-midi: 14:00 - 18:00 (8 créneaux de 30min)
                    debutSlots = new LocalTime[]{
                        // Matin
                        LocalTime.of(8, 0), LocalTime.of(8, 30),
                        LocalTime.of(9, 0), LocalTime.of(9, 30),
                        LocalTime.of(10, 0), LocalTime.of(10, 30),
                        LocalTime.of(11, 0), LocalTime.of(11, 30),
                        // Après-midi
                        LocalTime.of(14, 0), LocalTime.of(14, 30),
                        LocalTime.of(15, 0), LocalTime.of(15, 30),
                        LocalTime.of(16, 0), LocalTime.of(16, 30),
                        LocalTime.of(17, 0), LocalTime.of(17, 30)
                    };
                }
                
                // Créer chaque créneau
                for (LocalTime debut : debutSlots) {
                    LocalTime fin = debut.plusMinutes(30);
                    
                    // Samedi, fin à 13:00 max
                    if (jourSemaine == JourSemaine.SAMEDI && debut.isAfter(LocalTime.of(12, 0))) {
                        continue;
                    }
                    
                    // Vendredi après-midi, fin à 17:00 max
                    if (jourSemaine == JourSemaine.VENDREDI && debut.isAfter(LocalTime.of(16, 30))) {
                        continue;
                    }
                    
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

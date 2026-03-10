package com.medibook.common.config.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SeederRunner implements CommandLineRunner {

    private final List<Seeder> seeders;

    @Override
    public void run(String... args) {
        log.info("========================================");
        log.info("🚀 Démarrage du processus de seeding...");
        log.info("========================================");

        // Trier les seeders par ordre d'exécution
        List<Seeder> sortedSeeders = seeders.stream()
                .sorted(Comparator.comparingInt(Seeder::getOrder))
                .toList();

        for (Seeder seeder : sortedSeeders) {
            try {
                if (seeder.shouldRun()) {
                    log.info("➡️ Exécution du seeder: {} (ordre: {})", seeder.getName(), seeder.getOrder());
                    seeder.run();
                    log.info("✅ Seeder '{}' terminé avec succès", seeder.getName());
                } else {
                    log.info("⏭️ Seeder ignoré (données déjà existantes): {}", seeder.getName());
                }
            } catch (Exception e) {
                log.error("❌ Erreur lors de l'exécution du seeder '{}': {}", seeder.getName(), e.getMessage(), e);
            }
        }

        log.info("========================================");
        log.info("🏁 Processus de seeding terminé");
        log.info("========================================");
    }
}

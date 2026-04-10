package com.medibook.common.monitoring;

import com.medibook.cabinet.repository.CabinetRepository;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.StatutRdv;
import com.medibook.common.enums.Status;
import com.medibook.rendezvous.repository.RendezVousRepository;
import com.medibook.user.repository.UserRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class MediBookMetricsBinder implements MeterBinder {

    private final UserRepository userRepository;
    private final RendezVousRepository rendezVousRepository;
    private final CabinetRepository cabinetRepository;

    @Override
    public void bindTo(MeterRegistry registry) {
        registerUserGauges(registry);
        registerRendezVousGauges(registry);
        registerCabinetGauge(registry);
    }

    private void registerUserGauges(MeterRegistry registry) {
        registerGauge(registry, MediBookMetricNames.USERS_TOTAL, "Nombre total d'utilisateurs par role",
                () -> userRepository.countByRole(Role.PATIENT), "role", "patient");
        registerGauge(registry, MediBookMetricNames.USERS_TOTAL, "Nombre total d'utilisateurs par role",
                () -> userRepository.countByRole(Role.MEDECIN), "role", "medecin");
        registerGauge(registry, MediBookMetricNames.USERS_TOTAL, "Nombre total d'utilisateurs par role",
                () -> userRepository.countByRole(Role.SECRETAIRE), "role", "secretaire");
        registerGauge(registry, MediBookMetricNames.USERS_TOTAL, "Nombre total d'utilisateurs par role",
                () -> userRepository.countByRole(Role.ADMIN), "role", "admin");
        registerGauge(registry, MediBookMetricNames.USERS_TOTAL, "Nombre total d'utilisateurs par role",
                () -> userRepository.countByRole(Role.SUPER_ADMIN), "role", "super_admin");

        registerGauge(registry, MediBookMetricNames.ACTIVE_USERS_TOTAL, "Nombre d'utilisateurs actifs par role",
                () -> userRepository.countByRoleAndStatus(Role.PATIENT, Status.ACTIF), "role", "patient");
        registerGauge(registry, MediBookMetricNames.ACTIVE_USERS_TOTAL, "Nombre d'utilisateurs actifs par role",
                () -> userRepository.countByRoleAndStatus(Role.MEDECIN, Status.ACTIF), "role", "medecin");
        registerGauge(registry, MediBookMetricNames.ACTIVE_USERS_TOTAL, "Nombre d'utilisateurs actifs par role",
                () -> userRepository.countByRoleAndStatus(Role.SECRETAIRE, Status.ACTIF), "role", "secretaire");
        registerGauge(registry, MediBookMetricNames.ACTIVE_USERS_TOTAL, "Nombre d'utilisateurs actifs par role",
                () -> userRepository.countByRoleAndStatus(Role.ADMIN, Status.ACTIF), "role", "admin");
        registerGauge(registry, MediBookMetricNames.ACTIVE_USERS_TOTAL, "Nombre d'utilisateurs actifs par role",
                () -> userRepository.countByRoleAndStatus(Role.SUPER_ADMIN, Status.ACTIF), "role", "super_admin");
    }

    private void registerRendezVousGauges(MeterRegistry registry) {
        registerGauge(registry, MediBookMetricNames.RENDEZVOUS_TOTAL, "Nombre total de rendez-vous par statut",
                rendezVousRepository::count, "statut", "all");
        registerGauge(registry, MediBookMetricNames.RENDEZVOUS_TOTAL, "Nombre total de rendez-vous par statut",
                () -> rendezVousRepository.countByStatut(StatutRdv.EN_ATTENTE), "statut", "en_attente");
        registerGauge(registry, MediBookMetricNames.RENDEZVOUS_TOTAL, "Nombre total de rendez-vous par statut",
                () -> rendezVousRepository.countByStatut(StatutRdv.CONFIRME), "statut", "confirme");
        registerGauge(registry, MediBookMetricNames.RENDEZVOUS_TOTAL, "Nombre total de rendez-vous par statut",
                () -> rendezVousRepository.countByStatut(StatutRdv.ANNULE), "statut", "annule");
        registerGauge(registry, MediBookMetricNames.RENDEZVOUS_TOTAL, "Nombre total de rendez-vous par statut",
                () -> rendezVousRepository.countByStatut(StatutRdv.TERMINE), "statut", "termine");
    }

    private void registerCabinetGauge(MeterRegistry registry) {
        registerGauge(registry, MediBookMetricNames.CABINETS_TOTAL, "Nombre total de cabinets",
                cabinetRepository::count);
    }

    private void registerGauge(
            MeterRegistry registry,
            String name,
            String description,
            Supplier<Number> valueSupplier,
            String... tags
    ) {
        Gauge.builder(name, valueSupplier, supplier -> supplier.get().doubleValue())
                .description(description)
                .tags(tags)
                .register(registry);
    }
}

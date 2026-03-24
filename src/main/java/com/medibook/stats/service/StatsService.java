package com.medibook.stats.service;

import com.medibook.cabinet.repository.CabinetRepository;
import com.medibook.common.enums.Role;
import com.medibook.common.enums.StatutRdv;
import com.medibook.rendezvous.repository.RendezVousRepository;
import com.medibook.stats.dto.StatsResponse;
import com.medibook.user.entity.Utilisateur;
import com.medibook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final RendezVousRepository rendezVousRepository;
    private final UserRepository userRepository;
    private final CabinetRepository cabinetRepository;

    public StatsResponse getMedecinStats(Long medecinId) {
        return StatsResponse.builder()
                .totalRdv(rendezVousRepository.countByMedecinId(medecinId))
                .rdvEnAttente(rendezVousRepository.countByMedecinIdAndStatut(medecinId, StatutRdv.EN_ATTENTE))
                .rdvConfirmes(rendezVousRepository.countByMedecinIdAndStatut(medecinId, StatutRdv.CONFIRME))
                .rdvTermines(rendezVousRepository.countByMedecinIdAndStatut(medecinId, StatutRdv.TERMINE))
                .rdvAnnules(rendezVousRepository.countByMedecinIdAndStatut(medecinId, StatutRdv.ANNULE))
                .totalPatients(rendezVousRepository.countDistinctPatientsByMedecinId(medecinId))
                .build();
    }

    public StatsResponse getAdminStats(Utilisateur admin) {
        Long cabinetId = admin.getCabinet().getId();
        String cabinetNom = admin.getCabinet().getNom();
        return StatsResponse.builder()
                .cabinetNom(cabinetNom)
                .totalMedecins(userRepository.countByRoleAndCabinetId(Role.MEDECIN, cabinetId))
                .totalSecretaires(userRepository.countByRoleAndCabinetId(Role.SECRETAIRE, cabinetId))
                .totalPatients(rendezVousRepository.countDistinctPatientsByCabinetId(cabinetId))
                .totalRdv(rendezVousRepository.countByCabinetId(cabinetId))
                .rdvEnAttente(rendezVousRepository.countByCabinetIdAndStatut(cabinetId, StatutRdv.EN_ATTENTE))
                .rdvConfirmes(rendezVousRepository.countByCabinetIdAndStatut(cabinetId, StatutRdv.CONFIRME))
                .rdvTermines(rendezVousRepository.countByCabinetIdAndStatut(cabinetId, StatutRdv.TERMINE))
                .rdvAnnules(rendezVousRepository.countByCabinetIdAndStatut(cabinetId, StatutRdv.ANNULE))
                .build();
    }

    public StatsResponse getSuperAdminStats() {
        return StatsResponse.builder()
                .totalCabinets(cabinetRepository.count())
                .totalMedecins(userRepository.countByRole(Role.MEDECIN))
                .totalSecretaires(userRepository.countByRole(Role.SECRETAIRE))
                .totalPatients(userRepository.countByRole(Role.PATIENT))
                .totalRdv(rendezVousRepository.count())
                .rdvEnAttente(rendezVousRepository.countByStatut(StatutRdv.EN_ATTENTE))
                .rdvConfirmes(rendezVousRepository.countByStatut(StatutRdv.CONFIRME))
                .rdvTermines(rendezVousRepository.countByStatut(StatutRdv.TERMINE))
                .rdvAnnules(rendezVousRepository.countByStatut(StatutRdv.ANNULE))
                .build();
    }
}

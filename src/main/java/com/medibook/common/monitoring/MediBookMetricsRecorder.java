package com.medibook.common.monitoring;

import com.medibook.common.enums.Role;

public interface MediBookMetricsRecorder {

    void recordLoginSuccess();

    void recordLoginFailure(String reason);

    void recordRegistration(Role role);

    void recordUserCreated(Role role);

    void recordRendezVousEvent(String action, String actor);
}

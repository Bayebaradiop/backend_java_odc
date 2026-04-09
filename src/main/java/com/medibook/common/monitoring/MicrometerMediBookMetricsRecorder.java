package com.medibook.common.monitoring;

import com.medibook.common.enums.Role;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MicrometerMediBookMetricsRecorder implements MediBookMetricsRecorder {

    private final MeterRegistry meterRegistry;

    @Override
    public void recordLoginSuccess() {
        increment(MediBookMetricNames.AUTH_LOGINS_TOTAL, "result", "success");
    }

    @Override
    public void recordLoginFailure(String reason) {
        increment(MediBookMetricNames.AUTH_LOGINS_TOTAL, "result", "failure", "reason", normalize(reason));
    }

    @Override
    public void recordRegistration(Role role) {
        increment(MediBookMetricNames.AUTH_REGISTRATIONS_TOTAL, "role", normalize(role));
    }

    @Override
    public void recordUserCreated(Role role) {
        increment(MediBookMetricNames.USERS_CREATED_TOTAL, "role", normalize(role));
    }

    @Override
    public void recordRendezVousEvent(String action, String actor) {
        increment(MediBookMetricNames.RENDEZVOUS_EVENTS_TOTAL, "action", normalize(action), "actor", normalize(actor));
    }

    private void increment(String metricName, String... tags) {
        meterRegistry.counter(metricName, Tags.of(tags)).increment();
    }

    private String normalize(Role role) {
        return normalize(role.name());
    }

    private String normalize(String value) {
        return value.toLowerCase().replace(' ', '_');
    }
}

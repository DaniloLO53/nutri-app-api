package org.nutri.app.nutri_app_api.repositories.appointmentRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AppointmentNutritionistProjection {
    UUID getId();
    LocalDateTime getStartTime();
    Integer getDurationMinutes();
    UUID getPatientId();
    String getPatientEmail();
    String getPatientName();
    String getStatus();
}

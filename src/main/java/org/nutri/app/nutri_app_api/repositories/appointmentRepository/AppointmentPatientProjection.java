package org.nutri.app.nutri_app_api.repositories.appointmentRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AppointmentPatientProjection {
    UUID getId();
    LocalDateTime getStartTime();
    Integer getDurationMinutes();
    String getNutritionistName();
    String getNutritionistEmail();
    UUID getNutritionistId();
    String getStatus();
    Boolean isRemote();
}

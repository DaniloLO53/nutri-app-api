package org.nutri.app.nutri_app_api.repositories.appointmentRepository;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AppointmentNutritionistProjection {
    @NotNull UUID getId();
    @NotNull LocalDateTime getStartTime();
    @NotNull Integer getDurationMinutes();
    @NotNull UUID getPatientId();
    @NotNull String getPatientEmail();
    @NotNull String getPatientName();
    @NotNull String getStatus();
    @NotNull String getAddress();
    @NotNull Boolean getIsRemote();
}

package org.nutri.app.nutri_app_api.repositories.scheduleRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface OwnScheduleProjection {
    UUID getScheduleId();
    UUID getAppointmentId();
    String getAddress();
    UUID getLocationId();
    LocalDateTime getStartTime();
    Integer getDurationMinutes();
    String getType();
    String getPatientName();
    UUID getPatientId();
    String getPatientEmail();
    String getStatus();
}
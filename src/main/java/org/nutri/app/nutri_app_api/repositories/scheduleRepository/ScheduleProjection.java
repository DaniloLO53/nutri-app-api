package org.nutri.app.nutri_app_api.repositories.scheduleRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ScheduleProjection {
    UUID getId();
    LocalDateTime getStartTime();
    Integer getDurationMinutes();
}

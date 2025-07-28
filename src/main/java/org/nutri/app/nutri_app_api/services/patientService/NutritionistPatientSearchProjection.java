package org.nutri.app.nutri_app_api.services.patientService;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public interface NutritionistPatientSearchProjection {
    UUID getId();
    String getName();
    String getProfilePictureUrl();
    OffsetDateTime lastAppointmentDate();
}

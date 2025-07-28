package org.nutri.app.nutri_app_api.services.patientService;

import java.time.Instant;
import java.util.UUID;

public interface NutritionistPatientSearchProjection {
    UUID getId();
    String getName();
    String getProfilePictureUrl();
    Instant getLastAppointmentDate();
}

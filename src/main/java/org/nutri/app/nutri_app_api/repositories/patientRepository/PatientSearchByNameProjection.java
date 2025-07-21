package org.nutri.app.nutri_app_api.repositories.patientRepository;

import java.util.UUID;

public interface PatientSearchByNameProjection {
    UUID getId();
    String getFullName();
    String getEmail();
}

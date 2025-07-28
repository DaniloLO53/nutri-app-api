package org.nutri.app.nutri_app_api.services.patientService;

import org.nutri.app.nutri_app_api.payloads.patientDTOs.NutritionistPatientSearchDTO;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;
import java.util.UUID;

public interface PatientService {
    Set<PatientSearchByNameDTO> getProfilesByName(String name);
    Page<NutritionistPatientSearchDTO> getNutritionistPatients(UUID userId, Pageable pageable);
}

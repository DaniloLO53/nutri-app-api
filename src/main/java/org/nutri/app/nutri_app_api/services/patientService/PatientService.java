package org.nutri.app.nutri_app_api.services.patientService;

import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;

import java.util.Set;

public interface PatientService {
    Set<PatientSearchByNameDTO> getProfilesByName(String name);
}

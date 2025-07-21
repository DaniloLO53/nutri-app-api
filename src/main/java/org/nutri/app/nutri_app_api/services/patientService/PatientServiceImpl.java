package org.nutri.app.nutri_app_api.services.patientService;

import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;
import org.nutri.app.nutri_app_api.repositories.patientRepository.PatientRepository;
import org.nutri.app.nutri_app_api.repositories.patientRepository.PatientSearchByNameProjection;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Set<PatientSearchByNameDTO> getProfilesByName(String name) {
        if (name.isBlank()) return new HashSet<>();

        Set<PatientSearchByNameProjection> patients = patientRepository.findByName(name);

        return patients.stream().map(projection -> {
            PatientSearchByNameDTO patientDTO = new PatientSearchByNameDTO();

            patientDTO.setId(projection.getId());
            patientDTO.setName(projection.getFullName());
            patientDTO.setEmail(projection.getEmail());

            return patientDTO;
        }).collect(Collectors.toSet());
    }
}

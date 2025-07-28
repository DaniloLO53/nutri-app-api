package org.nutri.app.nutri_app_api.services.patientService;

import org.nutri.app.nutri_app_api.payloads.patientDTOs.NutritionistPatientSearchDTO;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;
import org.nutri.app.nutri_app_api.repositories.patientRepository.PatientRepository;
import org.nutri.app.nutri_app_api.repositories.patientRepository.PatientSearchByNameProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;

    public PatientServiceImpl(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public Page<NutritionistPatientSearchDTO> getNutritionistPatients(UUID userId, Pageable pageable) {
        Page<NutritionistPatientSearchProjection> projectionPage = patientRepository.findNutritionistPatients(userId, pageable);

        List<NutritionistPatientSearchDTO> dtos = projectionPage.getContent().stream().map(projection -> {
            NutritionistPatientSearchDTO dto = new NutritionistPatientSearchDTO();
            dto.setId(projection.getId());
            dto.setName(projection.getName());
            dto.setLastAppointmentDate(projection.lastAppointmentDate().toLocalDate());

            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, projectionPage.getTotalElements());
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

package org.nutri.app.nutri_app_api.services.patientService;

import org.nutri.app.nutri_app_api.exceptions.ConflictException;
import org.nutri.app.nutri_app_api.exceptions.ResourceNotFoundException;
import org.nutri.app.nutri_app_api.models.patientNutritionistRelationship.PatientNutritionistRelationship;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.NutritionistPatientSearchDTO;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;
import org.nutri.app.nutri_app_api.payloads.patientNutritionistRelationshipDTO.PatientNutritionistRelationshipDTO;
import org.nutri.app.nutri_app_api.repositories.nutritionistRepository.NutritionistRepository;
import org.nutri.app.nutri_app_api.repositories.patientNutritionistRelationshipRepository.PatientNutritionistRelationshipRepository;
import org.nutri.app.nutri_app_api.repositories.patientRepository.PatientRepository;
import org.nutri.app.nutri_app_api.repositories.patientRepository.PatientSearchByNameProjection;
import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;
import org.nutri.app.nutri_app_api.security.models.users.Patient;
import org.nutri.app.nutri_app_api.security.repositories.AuthRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PatientServiceImpl implements PatientService {
    private final PatientRepository patientRepository;
    private final NutritionistRepository nutritionistRepository;
    private final PatientNutritionistRelationshipRepository patientNutritionistRelationshipRepository;

    public PatientServiceImpl(PatientRepository patientRepository,
                              NutritionistRepository nutritionistRepository,
                              PatientNutritionistRelationshipRepository patientNutritionistRelationshipRepository) {
        this.patientRepository = patientRepository;
        this.nutritionistRepository = nutritionistRepository;
        this.patientNutritionistRelationshipRepository = patientNutritionistRelationshipRepository;
    }

    @Override
    public PatientNutritionistRelationshipDTO createNutritionistPatient(UUID userId, PatientNutritionistRelationshipDTO requestDTO) {
        UUID patientId = requestDTO.getPatientId();
        Nutritionist nutritionist = nutritionistRepository
                .findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId.toString()));
        Patient patient = patientRepository
                .findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", patientId.toString()));

        boolean relationshipAlreadyExists = patientNutritionistRelationshipRepository
                .existsByNutritionistIdAndPatientId(nutritionist.getId(), patientId);
        if (relationshipAlreadyExists) {
            throw new ConflictException("Esse relacionamento já foi criado");
        }

        PatientNutritionistRelationship relationship = new PatientNutritionistRelationship();

        nutritionist.getRelationships().add(relationship);
        relationship.setNutritionist(nutritionist);

        patient.getRelationships().add(relationship);
        relationship.setPatient(patient);

        LocalDate now = LocalDate.now();
        relationship.setStartDate(now);

        PatientNutritionistRelationship savedRelationship = patientNutritionistRelationshipRepository.save(relationship);

        PatientNutritionistRelationshipDTO dto  = new PatientNutritionistRelationshipDTO();
        dto.setPatientId(savedRelationship.getPatient().getId());
        dto.setId(savedRelationship.getId());

        return dto;
    }

    @Override
    public Page<NutritionistPatientSearchDTO> getNutritionistScheduledPatientsByName(UUID userId, String name, Pageable pageable) {
        if (name.isBlank()) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
        Page<NutritionistPatientSearchProjection> projectionPage = patientRepository.findNutritionistScheduledPatientsByName(userId, name, pageable);

        List<NutritionistPatientSearchDTO> dtos = projectionPage.getContent().stream().map(projection -> {
            NutritionistPatientSearchDTO dto = new NutritionistPatientSearchDTO();
            dto.setId(projection.getId());
            dto.setName(projection.getName());

            return dto;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, projectionPage.getTotalElements());
    }

    @Override
    public Page<NutritionistPatientSearchDTO> getNutritionistPatients(UUID userId, Pageable pageable) {
        Nutritionist nutritionist = nutritionistRepository
                .findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId.toString()));
        Page<NutritionistPatientSearchProjection> projectionPage = patientRepository.findNutritionistPatients(nutritionist.getId(), pageable);

        List<NutritionistPatientSearchDTO> dtos = projectionPage.getContent().stream().map(projection -> {
            NutritionistPatientSearchDTO dto = new NutritionistPatientSearchDTO();
            dto.setId(projection.getId());
            dto.setName(projection.getName());
            if (projection.getLastAppointmentDate() != null) {
            // Converte o Instant (UTC) para um LocalDate no fuso horário do sistema
            LocalDate localDate = projection.getLastAppointmentDate()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate();
            dto.setLastAppointmentDate(localDate);
        }

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

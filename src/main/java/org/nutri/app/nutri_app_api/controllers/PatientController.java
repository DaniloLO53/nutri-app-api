package org.nutri.app.nutri_app_api.controllers;

import jakarta.validation.Valid;
import org.nutri.app.nutri_app_api.payloads.PaginatedResponseDTO;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.NutritionistPatientSearchDTO;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;
import org.nutri.app.nutri_app_api.payloads.patientNutritionistRelationshipDTO.PatientNutritionistRelationshipDTO;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.nutri.app.nutri_app_api.services.patientService.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping("/patients/search")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<Set<PatientSearchByNameDTO>> getProfiles(@RequestParam(name = "name", required = false) String name) {

        Set<PatientSearchByNameDTO> profiles = patientService.getProfilesByName(name);

        return ResponseEntity.status(HttpStatus.OK).body(profiles);
    }

    @GetMapping("/nutritionists/me/patients")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<PaginatedResponseDTO<NutritionistPatientSearchDTO>> getNutritionistPatients(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Page<NutritionistPatientSearchDTO> patientPage = patientService.getNutritionistPatients(userId, pageable);
        PaginatedResponseDTO<NutritionistPatientSearchDTO> response = new PaginatedResponseDTO<>(patientPage);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/nutritionists/me/patients/scheduled")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<PaginatedResponseDTO<NutritionistPatientSearchDTO>> getNutritionistScheduledPatientsByName(
            Authentication authentication,
            @RequestParam(name = "name", defaultValue = "") String name,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Page<NutritionistPatientSearchDTO> patientPage = patientService.getNutritionistScheduledPatientsByName(userId, name, pageable);
        PaginatedResponseDTO<NutritionistPatientSearchDTO> response = new PaginatedResponseDTO<>(patientPage);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/nutritionists/me/patients")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<PatientNutritionistRelationshipDTO> createNutritionistPatient(
            Authentication authentication,
            @RequestBody @Valid PatientNutritionistRelationshipDTO requestDTO) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();
        PatientNutritionistRelationshipDTO response = patientService.createNutritionistPatient(userId, requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

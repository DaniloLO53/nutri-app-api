package org.nutri.app.nutri_app_api.controllers;

import org.nutri.app.nutri_app_api.payloads.patientDTOs.NutritionistPatientSearchDTO;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.nutri.app.nutri_app_api.services.patientService.PatientService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<Page<NutritionistPatientSearchDTO>> getNutritionistPatients(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Page<NutritionistPatientSearchDTO> patients = patientService.getNutritionistPatients(userId, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(patients);
    }
}

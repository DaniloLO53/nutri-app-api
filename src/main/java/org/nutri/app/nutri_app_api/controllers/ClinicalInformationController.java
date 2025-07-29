package org.nutri.app.nutri_app_api.controllers;

import org.nutri.app.nutri_app_api.payloads.clinicalInformation.ClinicalInformationDTO;
import org.nutri.app.nutri_app_api.payloads.clinicalInformation.ClinicalInformationMasterDataDTO;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.nutri.app.nutri_app_api.services.clinicalInformationService.ClinicalInformationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ClinicalInformationController {
    private final ClinicalInformationService clinicalInformationService;

    public ClinicalInformationController(ClinicalInformationService clinicalInformationService) {
        this.clinicalInformationService = clinicalInformationService;
    }

    @GetMapping("/patients/{patientId}/clinical-information")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<?> getClinicalInformation(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID patientId) {
        ClinicalInformationDTO clinicalInformation = clinicalInformationService.getClinicalInformation(userDetails.getId(), patientId);

        return ResponseEntity.status(HttpStatus.OK).body(clinicalInformation);
    }

    @GetMapping("/clinical-information/master-data")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<ClinicalInformationMasterDataDTO> getClinicalInformationMasterData() {
        ClinicalInformationMasterDataDTO data = clinicalInformationService.getClinicalInformationMasterData();

        return ResponseEntity.status(HttpStatus.OK).body(data);
    }
}
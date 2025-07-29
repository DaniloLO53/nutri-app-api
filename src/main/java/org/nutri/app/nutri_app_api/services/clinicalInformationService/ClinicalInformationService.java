package org.nutri.app.nutri_app_api.services.clinicalInformationService;

import org.nutri.app.nutri_app_api.payloads.clinicalInformation.ClinicalInformationDTO;
import org.nutri.app.nutri_app_api.payloads.clinicalInformation.ClinicalInformationMasterDataDTO;

import java.util.UUID;

public interface ClinicalInformationService {
    ClinicalInformationDTO getClinicalInformation(UUID userId, UUID patientId);
    ClinicalInformationMasterDataDTO getClinicalInformationMasterData();
}

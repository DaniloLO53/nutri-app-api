package org.nutri.app.nutri_app_api.services.clinicalInformationService;

import org.modelmapper.ModelMapper;
import org.nutri.app.nutri_app_api.models.clinicalInformations.ClinicalInformation;
import org.nutri.app.nutri_app_api.payloads.clinicalInformation.ClinicalInformationDTO;
import org.nutri.app.nutri_app_api.repositories.clinicalInformationRepository.ClinicalInformationRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClinicalInformationServiceImpl implements ClinicalInformationService {
    private final ClinicalInformationRepository clinicalInformationRepository;
    private final ModelMapper modelMapper;

    public ClinicalInformationServiceImpl(ClinicalInformationRepository clinicalInformationRepository, ModelMapper modelMapper) {
        this.clinicalInformationRepository = clinicalInformationRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ClinicalInformationDTO getClinicalInformation(UUID userId, UUID patientId) {
        System.out.println("Patient id: " + patientId);
        ClinicalInformation info = clinicalInformationRepository.findMostRecentByPatientAndNutritionistNative(patientId, userId);

        if (info != null) {
            return modelMapper.map(info, ClinicalInformationDTO.class);
        } else {
            return null;
        }
    }
}

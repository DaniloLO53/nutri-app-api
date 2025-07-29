package org.nutri.app.nutri_app_api.services.clinicalInformationService;

import org.modelmapper.ModelMapper;
import org.nutri.app.nutri_app_api.models.clinicalInformations.ClinicalInformation;
import org.nutri.app.nutri_app_api.models.clinicalInformations.allergens.AllergenType;
import org.nutri.app.nutri_app_api.models.clinicalInformations.medications.MedicationType;
import org.nutri.app.nutri_app_api.payloads.clinicalInformation.ClinicalInformationDTO;
import org.nutri.app.nutri_app_api.payloads.clinicalInformation.ClinicalInformationMasterDataDTO;
import org.nutri.app.nutri_app_api.payloads.clinicalInformation.masterData.*;
import org.nutri.app.nutri_app_api.repositories.clinicalInformationRepository.ClinicalInformationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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
    public ClinicalInformationMasterDataDTO getClinicalInformationMasterData() {
        List<MasterDataProjection> projections = clinicalInformationRepository.findClinicalInformationMasterData();
        return buildMasterDataFromProjection(projections);
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

    private ClinicalInformationMasterDataDTO buildMasterDataFromProjection(List<MasterDataProjection> projections) {
        // Inicializa as listas que irão compor o resultado final
        List<MasterSymptomDTO> symptomList = new ArrayList<>();
        List<MasterAllergenDTO> allergenList = new ArrayList<>();
        List<MasterDiseaseDTO> diseaseList = new ArrayList<>();
        List<MasterMedicationDTO> medicationList = new ArrayList<>();
        List<MasterFoodDTO> foodList = new ArrayList<>();

        // Itera sobre cada item da projeção e o coloca na lista correta
        for (MasterDataProjection proj : projections) {

            switch (ClinicalInformationMasterDataType.valueOf(proj.getSource())) {
                case ClinicalInformationMasterDataType.SYMPTOM:
                    symptomList.add(new MasterSymptomDTO(proj.getId(), proj.getName()));
                    break;
                case ClinicalInformationMasterDataType.DISEASE:
                    diseaseList.add(new MasterDiseaseDTO(proj.getId(), proj.getName()));
                    break;
                case ClinicalInformationMasterDataType.ALLERGEN:
                    AllergenType allergenType = AllergenType.valueOf(proj.getType());
                    allergenList.add(new MasterAllergenDTO(proj.getId(), proj.getName(), allergenType));
                    break;
                case ClinicalInformationMasterDataType.MEDICATION_SUPPLEMENT:
                    MedicationType medType = MedicationType.valueOf(proj.getType());
                    medicationList.add(new MasterMedicationDTO(proj.getId(), proj.getName(), medType));
                    break;
                case ClinicalInformationMasterDataType.FOOD:
                    foodList.add(new MasterFoodDTO(proj.getId(), proj.getName()));
                    break;
                default:
                    // Opcional: Logar um aviso se uma fonte desconhecida aparecer
                    break;
            }
        }

        // Constrói o DTO final com todas as listas populadas
        return new ClinicalInformationMasterDataDTO(
            symptomList,
            allergenList,
            diseaseList,
            medicationList,
            foodList
        );
    }
}

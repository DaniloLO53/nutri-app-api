package org.nutri.app.nutri_app_api.payloads.clinicalInformation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutri.app.nutri_app_api.payloads.clinicalInformation.masterData.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClinicalInformationMasterDataDTO {
    private @NotNull List<MasterSymptomDTO> symptoms;
    private @NotNull List<MasterAllergenDTO> allergens;
    private @NotNull List<MasterDiseaseDTO> diseases;
    private @NotNull List<MasterMedicationDTO> medications;
    private @NotNull List<MasterFoodDTO> foodPreferencesAndAversions;
}

package org.nutri.app.nutri_app_api.payloads.clinicalInformation.masterData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutri.app.nutri_app_api.models.clinicalInformations.allergens.AllergenType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterDiseaseDTO {
    private UUID symptomId;
    private String name;
}
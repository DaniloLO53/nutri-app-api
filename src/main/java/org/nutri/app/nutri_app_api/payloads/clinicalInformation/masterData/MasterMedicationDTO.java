package org.nutri.app.nutri_app_api.payloads.clinicalInformation.masterData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutri.app.nutri_app_api.models.clinicalInformations.medications.MedicationType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterMedicationDTO {
    private UUID medicationId;
    private String name;
    private MedicationType type;
}

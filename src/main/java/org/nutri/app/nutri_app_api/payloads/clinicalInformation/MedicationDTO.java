package org.nutri.app.nutri_app_api.payloads.clinicalInformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutri.app.nutri_app_api.models.clinicalInformations.medications.MedicationType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationDTO {
    private UUID medicationId;
    private String name;
    private String dosage;
    private String notes;
//    private MedicationType type;
}

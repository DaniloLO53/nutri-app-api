package org.nutri.app.nutri_app_api.payloads.clinicalInformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SymptomDTO {
    private UUID symptomId;
    private String name;
    private Integer intensity;
    private String frequency;
    private String duration;
    private String notes;
}

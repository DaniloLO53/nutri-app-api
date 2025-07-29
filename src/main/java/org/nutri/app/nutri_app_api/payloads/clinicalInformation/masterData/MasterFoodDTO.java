package org.nutri.app.nutri_app_api.payloads.clinicalInformation.masterData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterFoodDTO {
    private UUID id;
    private String name;
}
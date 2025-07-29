package org.nutri.app.nutri_app_api.payloads.clinicalInformation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutri.app.nutri_app_api.models.clinicalInformations.allergens.AllergenType;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllergenDTO {
    private UUID allergenId;
    private String name;
    private String reactionDetails;
//    private AllergenType type;
}

package org.nutri.app.nutri_app_api.models.clinicalInformations.foods;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class InformationFoodId implements Serializable {
    private UUID informationId;
    private UUID foodId;
}

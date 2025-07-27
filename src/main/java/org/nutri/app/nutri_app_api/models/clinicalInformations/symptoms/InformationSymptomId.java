package org.nutri.app.nutri_app_api.models.clinicalInformations.symptoms;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // Essencial para chaves compostas
public class InformationSymptomId implements Serializable {
    private UUID informationId;
    private UUID symptomId;
}
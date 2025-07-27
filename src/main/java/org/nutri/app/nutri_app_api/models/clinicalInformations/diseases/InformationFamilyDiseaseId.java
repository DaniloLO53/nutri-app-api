package org.nutri.app.nutri_app_api.models.clinicalInformations.diseases;

import jakarta.persistence.Column;
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
public class InformationFamilyDiseaseId implements Serializable {
    private UUID informationId;
    private UUID diseaseId;

    @Column(name = "family_member", length = 100)
    private String familyMember;
}
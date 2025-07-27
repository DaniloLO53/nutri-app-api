package org.nutri.app.nutri_app_api.models.clinicalInformations.diseases;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.nutri.app.nutri_app_api.models.clinicalInformations.ClinicalInformation;

@Entity
@Table(name = "information_family_diseases")
@Getter
@Setter
public class InformationFamilyDisease {

    @EmbeddedId
    private InformationFamilyDiseaseId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("informationId")
    @JoinColumn(name = "information_id")
    private ClinicalInformation clinicalInformation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("diseaseId")
    @JoinColumn(name = "disease_id")
    private Disease disease;

    // O campo 'familyMember' é acessado através de 'id.getFamilyMember()'
}
package org.nutri.app.nutri_app_api.models.clinicalInformations.medications;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.nutri.app.nutri_app_api.models.clinicalInformations.ClinicalInformation;

@Entity
@Table(name = "information_medications")
@Getter
@Setter
public class InformationMedication {
    @EmbeddedId
    private InformationMedicationId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("informationId")
    @JoinColumn(name = "information_id")
    private ClinicalInformation clinicalInformation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("medicationId")
    @JoinColumn(name = "medication_id")
    private MedicationSupplement medicationSupplement;

    @Column(name = "dosage", columnDefinition = "TEXT")
    private String dosage;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}

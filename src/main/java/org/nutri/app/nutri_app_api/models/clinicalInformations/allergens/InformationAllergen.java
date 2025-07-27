package org.nutri.app.nutri_app_api.models.clinicalInformations.allergens;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.nutri.app.nutri_app_api.models.clinicalInformations.ClinicalInformation;

@Entity
@Table(name = "information_allergens")
@Getter
@Setter
public class InformationAllergen {

    @EmbeddedId
    private InformationAllergenId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("informationId")
    @JoinColumn(name = "information_id")
    private ClinicalInformation clinicalInformation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("allergenId")
    @JoinColumn(name = "allergen_id")
    private Allergen allergen;

    @Column(name = "reaction_details", columnDefinition = "TEXT")
    private String reactionDetails;
}
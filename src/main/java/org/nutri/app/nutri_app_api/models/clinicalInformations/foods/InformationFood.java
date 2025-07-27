package org.nutri.app.nutri_app_api.models.clinicalInformations.foods;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.nutri.app.nutri_app_api.models.clinicalInformations.ClinicalInformation;

@Entity
@Table(name = "information_foods")
@Getter
@Setter
public class InformationFood {

    @EmbeddedId
    private InformationFoodId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("informationId")
    @JoinColumn(name = "information_id")
    private ClinicalInformation clinicalInformation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("foodId")
    @JoinColumn(name = "food_id")
    private Food food;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private FoodPreferenceType type;
}

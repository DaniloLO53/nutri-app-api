package org.nutri.app.nutri_app_api.payloads.patientNutritionistRelationshipDTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PatientNutritionistRelationshipDTO {
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;
    private @NotNull UUID patientId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String relationshipNotes;
    private String terminationReason;
    private boolean canPatientScheduleFreely = true;
}


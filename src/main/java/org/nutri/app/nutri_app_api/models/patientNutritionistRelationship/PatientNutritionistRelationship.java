package org.nutri.app.nutri_app_api.models.patientNutritionistRelationship;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;
import org.nutri.app.nutri_app_api.security.models.users.Patient;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "patient_nutritionist_relationships",
    uniqueConstraints = {
        // Mapeia a constraint UNIQUE (nutritionist_id, patient_id) do SQL
        @UniqueConstraint(
            name = "unique_nutritionist_patient_relationship",
            columnNames = {"nutritionist_id", "patient_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class PatientNutritionistRelationship {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nutritionist_id", nullable = false)
    private Nutritionist nutritionist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "relationship_notes", columnDefinition = "TEXT")
    private String relationshipNotes;

    @Column(name = "termination_reason", columnDefinition = "TEXT")
    private String terminationReason;

    @Column(name = "can_patient_schedule_freely", nullable = false)
    private boolean canPatientScheduleFreely = true;
}

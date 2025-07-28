package org.nutri.app.nutri_app_api.security.models.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.nutri.app.nutri_app_api.models.appointments.Appointment;
import org.nutri.app.nutri_app_api.models.patientNutritionistRelationship.PatientNutritionistRelationship;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Patient {
    public Patient(String cpf) {
        this.cpf = cpf;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    @JdbcTypeCode(SqlTypes.UUID) // Hint for Hibernate to use native UUID type from database, if available
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 20,
            message = "Field cpf must have between 3 and 20 characters"
    )
    @Column(name = "cpf", nullable = false, unique = true)
    @ToString.Include
    private String cpf;

    @Past(message = "Field birthday must be a past date")
    @Column(name = "birthday")
    @ToString.Include
    private LocalDate birthday;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    // PERSIST - when we save a new patient with new appointments, these appointments will be saved with the patient
    // MERGE - when we update a pre-existing patient and add an appointment to it, this appointment will be saved with the patient.
    // Difference between CascadeTpe.REMOVE and orphanRemoval = true: the first one is executed when the father entity is deleted. The second one, when the child entity is deleted.
    // With CascadeTpe.REMOVE, when we do fatherRep.delete(father), the children are also removed.
    // In this application, if we do patientsRep.remove(patient), all appointments associated with this patient would be removed (we don't want that because we want to save the history)
    // With orphanRemoval = true, when we do father.getChildren().remove(child_01) and fatherRep.save(father), child_01 is now orphan and will be removed automatically (no need to do childRep.remove(child_01))
    // In this application, if we do patient.getAppointments().remove(app_01) and patientsRep.save(patient), app_01 is now orphan and will be removed (we want that)
    @OneToMany(mappedBy = "patient", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
    private Set<Appointment> appointments = new HashSet<>();

    @OneToMany(mappedBy = "patient", cascade = { CascadeType.ALL }, orphanRemoval = true)
    private Set<PatientNutritionistRelationship> relationships;
}

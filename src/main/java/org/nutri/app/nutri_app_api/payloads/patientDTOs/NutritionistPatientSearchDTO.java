package org.nutri.app.nutri_app_api.payloads.patientDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionistPatientSearchDTO {
    private UUID id;
    private String name;
    private String profilePictureUrl;
    private LocalDate lastAppointmentDate;
}

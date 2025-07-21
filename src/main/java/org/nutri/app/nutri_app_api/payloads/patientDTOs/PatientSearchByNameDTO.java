package org.nutri.app.nutri_app_api.payloads.patientDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PatientSearchByNameDTO {
    @NotNull
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private String email;
}

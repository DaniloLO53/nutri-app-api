package org.nutri.app.nutri_app_api.payloads.appointmentDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAppointmentDTO {
    private @NotNull UUID patientId;
    private UUID scheduleId;
    private @NotNull Boolean isRemote;
}

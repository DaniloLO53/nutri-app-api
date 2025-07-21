package org.nutri.app.nutri_app_api.payloads.appointmentDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatusName;
import org.nutri.app.nutri_app_api.validations.allowedDurations.AllowedDurations;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@NoArgsConstructor
public class AppointmentDTO {
    private @NotNull String id;
    private @NotNull AppointmentPatientDTO patient;
    private @NotNull LocalDateTime startTime;
    private @NotNull @AllowedDurations Integer durationMinutes;
    private @NotNull EventType type;
    private @NotNull AppointmentStatusName status;
}
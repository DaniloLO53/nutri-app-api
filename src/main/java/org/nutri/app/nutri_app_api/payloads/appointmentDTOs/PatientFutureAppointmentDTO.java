package org.nutri.app.nutri_app_api.payloads.appointmentDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatusName;
import org.nutri.app.nutri_app_api.validations.allowedDurations.AllowedDurations;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class PatientFutureAppointmentDTO {
    private @NotNull @EqualsAndHashCode.Include String id;
    private @NotNull PatientFutureAppointmentNutritionistDTO nutritionist;
    private @NotNull LocalDateTime startTime;
    private @NotNull @AllowedDurations Integer durationMinutes;
    private @NotNull EventType type;
    private @NotNull AppointmentStatusName status;
    private @NotNull Boolean isRemote;
    private @NotNull String address;
}

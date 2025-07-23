package org.nutri.app.nutri_app_api.payloads.scheduleDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatusName;
import org.nutri.app.nutri_app_api.payloads.locationDTOs.OwnLocationResponse;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;
import org.nutri.app.nutri_app_api.validations.allowedDurations.AllowedDurations;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnScheduleDTO {
    private UUID id;
    private @NotNull LocalDateTime startTime;
    private @AllowedDurations @NotNull Integer durationMinutes;
    private @NotNull AppointmentOrSchedule type;
    private @NotNull PatientSearchByNameDTO patient;
    private @NotNull AppointmentStatusName status;
    private @NotNull OwnLocationResponse location;
}

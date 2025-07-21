package org.nutri.app.nutri_app_api.payloads.appointmentDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatusName;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.AppointmentOrSchedule;
import org.nutri.app.nutri_app_api.validations.allowedDurations.AllowedDurations;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseToCreateAppointment {
    private UUID id;

    @NotNull
    private LocalDateTime startTime;

    @AllowedDurations
    private Integer durationMinutes;

    @NotNull
    private AppointmentOrSchedule type;

    @NotNull
    private PatientSearchByNameDTO patient;

    @NotNull
    private AppointmentStatusName status;
}

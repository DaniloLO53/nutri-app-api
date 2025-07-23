package org.nutri.app.nutri_app_api.payloads.scheduleDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nutri.app.nutri_app_api.validations.allowedDurations.AllowedDurations;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCreateDTO {
    private UUID id;
    @NotNull @ToString.Include private CustomLocalDateTime startLocalDateTime;
    @NotNull @AllowedDurations private Integer durationMinutes;
}

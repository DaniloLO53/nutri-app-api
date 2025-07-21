package org.nutri.app.nutri_app_api.payloads.scheduleDTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutri.app.nutri_app_api.validations.allowedDurations.AllowedDurations;

// TODO: validar se a data é valida (por exemplo, nao é valido 31/02/2025)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomLocalDateTime {
    @NotNull
    private Integer year;

    @NotNull
    @Min(1)
    @Max(12)
    private Integer month;

    @NotNull
    @Min(1)
    @Max(31)
    private Integer day;

    @NotNull
    @Min(1)
    @Max(23)
    private Integer hour;

    @NotNull
    @AllowedDurations
    @Max(45)
    private Integer minute;
}

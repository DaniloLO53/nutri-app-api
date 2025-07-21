package org.nutri.app.nutri_app_api.payloads.scheduleDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleSearchDTO {
    @ToString.Include
    private String id;

    @NotNull
    @ToString.Include
    private String nutritionistName;

    @NotNull
    @ToString.Include
    private String ibgeApiCity;

    @NotNull
    @ToString.Include
    private String ibgeApiState;

    @ToString.Include
    private String acceptsRemote;
}

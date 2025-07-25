package org.nutri.app.nutri_app_api.payloads.nutritionistDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSearchParamsDTO {
    @ToString.Include
    private String nutritionistName;

    @ToString.Include
    private String id;

    @NotNull
    @ToString.Include
    private String address;

    @NotNull
    @ToString.Include
    private String ibgeApiCity;

    @NotNull
    @ToString.Include
    private String ibgeApiState;

    @ToString.Include
    private Boolean acceptsRemote;
}

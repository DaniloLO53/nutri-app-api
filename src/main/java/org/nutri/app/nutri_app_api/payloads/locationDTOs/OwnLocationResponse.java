package org.nutri.app.nutri_app_api.payloads.locationDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OwnLocationResponse {
    private @NotNull String id;
    private @NotNull String address;
}

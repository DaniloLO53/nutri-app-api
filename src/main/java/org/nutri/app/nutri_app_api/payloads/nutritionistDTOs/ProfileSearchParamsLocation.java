package org.nutri.app.nutri_app_api.payloads.nutritionistDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSearchParamsLocation {
    private UUID id;
    private String address;
}

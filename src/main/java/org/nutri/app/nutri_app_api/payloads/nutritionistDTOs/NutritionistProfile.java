package org.nutri.app.nutri_app_api.payloads.nutritionistDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nutri.app.nutri_app_api.payloads.locationDTOs.LocationDTO;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutritionistProfile {
    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 20,
            message = "Field crf must have between 3 and 20 characters"
    )
    private String crf;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;

    @NotNull
    private String email;

    @NotNull
    private Boolean acceptsRemote;

    @NotNull
    private Set<LocationDTO> locations;
}

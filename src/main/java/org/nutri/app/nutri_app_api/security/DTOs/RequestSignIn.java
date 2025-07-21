package org.nutri.app.nutri_app_api.security.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestSignIn {
    @NotNull
    @NotBlank
    @Email
    @Size(
            min = 3,
            max = 100,
            message = "Field email must have between 3 and 100 characters"
    )
    private String email;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 255,
            message = "Field password must have between 3 and 255 characters"
    )
    private String password;
}

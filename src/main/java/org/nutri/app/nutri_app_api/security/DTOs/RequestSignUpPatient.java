package org.nutri.app.nutri_app_api.security.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestSignUpPatient {
    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 50,
            message = "Field firstName must have between 3 and 50 characters"
    )
    private String firstName;

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 50,
            message = "Field lastName must have between 3 and 50 characters"
    )
    private String lastName;

    @NotNull
    @NotBlank
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

    @NotNull
    @NotBlank
    @Size(
            min = 3,
            max = 255,
            message = "Field passwordConfirmation must have between 3 and 255 characters"
    )
    private String passwordConfirmation;

    @NotNull
    @NotBlank
    @Size(
            min = 11,
            max = 11,
            message = "Field cpf must have 11 characters"
    )
    private String cpf;
}

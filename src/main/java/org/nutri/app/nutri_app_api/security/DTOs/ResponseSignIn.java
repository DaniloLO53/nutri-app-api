package org.nutri.app.nutri_app_api.security.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseCookie;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseSignIn {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private String cpf;
    private LocalDate birthday;
    private String crf;
    private ResponseCookie jwtCookie;
}
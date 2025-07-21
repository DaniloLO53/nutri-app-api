package org.nutri.app.nutri_app_api.payloads.responseDTOs;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIExceptionResponse {
    private String message;
    private Integer statusCode;
}

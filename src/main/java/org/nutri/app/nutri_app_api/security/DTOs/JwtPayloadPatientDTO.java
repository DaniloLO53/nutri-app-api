package org.nutri.app.nutri_app_api.security.DTOs;

import org.nutri.app.nutri_app_api.security.models.users.RoleName;

import java.util.UUID;

public record JwtPayloadPatientDTO(
        UUID id,
        String email,
        RoleName role
) {}

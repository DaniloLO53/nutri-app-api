package org.nutri.app.nutri_app_api.payloads.notificationDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public interface NotificationUser {
    @NotNull UUID id();
    @NotNull @Email String email();
    @NotNull String name();
}

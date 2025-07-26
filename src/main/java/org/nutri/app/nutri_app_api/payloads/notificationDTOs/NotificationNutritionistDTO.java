package org.nutri.app.nutri_app_api.payloads.notificationDTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationNutritionistDTO implements NotificationUser {
    private @NotNull UUID id;
    private @NotNull @Email String email;
    private @NotNull String name;

    @Override
    public UUID id() {
        return id;
    }

    @Override
    public String email() {
        return email;
    }

    @Override
    public String name() {
        return name;
    }
}

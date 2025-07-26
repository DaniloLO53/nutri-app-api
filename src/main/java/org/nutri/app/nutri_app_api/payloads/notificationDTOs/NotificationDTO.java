package org.nutri.app.nutri_app_api.payloads.notificationDTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    private UUID id;
    private NotificationUser from;
    private @NotNull NotificationUser to;
    private String message;
    private @NotNull boolean isRead;
    private UUID relatedEntityId; // ID da consulta
    private @NotNull LocalDateTime createdAt;
}

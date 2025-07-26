package org.nutri.app.nutri_app_api.controllers;

import org.nutri.app.nutri_app_api.payloads.notificationDTOs.NotificationDTO;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.nutri.app.nutri_app_api.services.notificationService.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getMyNotifications(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        // Crie um método no seu serviço para buscar as notificações pelo UserDetails
        List<NotificationDTO> notifications = notificationService.getNotificationsForUser(userDetails.getId());
        return ResponseEntity.ok(notifications);
    }

    @PatchMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        // Crie um método no serviço para marcar como lida,
        // garantindo que o `userDetails.getId()` é o dono da notificação
        notificationService.markAsRead(userDetails.getId(), notificationId);
        return ResponseEntity.noContent().build();
    }
}
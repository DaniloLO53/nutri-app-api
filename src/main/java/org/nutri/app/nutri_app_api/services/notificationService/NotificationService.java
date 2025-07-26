package org.nutri.app.nutri_app_api.services.notificationService;

import org.nutri.app.nutri_app_api.payloads.notificationDTOs.NotificationDTO;

import java.util.List;
import java.util.UUID;

public interface NotificationService {
    void notifyPatientOfAppointment(UUID appointmentId);
    List<NotificationDTO> getNotificationsForUser(UUID userId);
}

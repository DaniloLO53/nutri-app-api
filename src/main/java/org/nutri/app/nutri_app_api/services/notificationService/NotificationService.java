package org.nutri.app.nutri_app_api.services.notificationService;

import java.util.UUID;

public interface NotificationService {
    void notifyPatientOfAppointment(UUID appointmentId);
}

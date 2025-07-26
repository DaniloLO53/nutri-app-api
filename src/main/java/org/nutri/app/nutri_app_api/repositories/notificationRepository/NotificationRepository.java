package org.nutri.app.nutri_app_api.repositories.notificationRepository;

import org.nutri.app.nutri_app_api.models.notifications.Notification;
import org.nutri.app.nutri_app_api.security.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);
}
package org.nutri.app.nutri_app_api.services.notificationService;

import org.nutri.app.nutri_app_api.exceptions.ForbiddenException;
import org.nutri.app.nutri_app_api.exceptions.ResourceNotFoundException;
import org.nutri.app.nutri_app_api.models.appointments.Appointment;
import org.nutri.app.nutri_app_api.models.notifications.Notification;
import org.nutri.app.nutri_app_api.payloads.notificationDTOs.NotificationDTO;
import org.nutri.app.nutri_app_api.payloads.notificationDTOs.NotificationNutritionistDTO;
import org.nutri.app.nutri_app_api.payloads.notificationDTOs.NotificationPatientDTO;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentRepository;
import org.nutri.app.nutri_app_api.repositories.notificationRepository.NotificationRepository;
import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;
import org.nutri.app.nutri_app_api.security.models.users.Patient;
import org.nutri.app.nutri_app_api.security.models.users.User;
import org.nutri.app.nutri_app_api.security.repositories.AuthRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final AppointmentRepository appointmentRepository;
    private final NotificationRepository notificationRepository;
    private final AuthRepository authRepository;

    public NotificationServiceImpl(SimpMessagingTemplate messagingTemplate, AppointmentRepository appointmentRepository, NotificationRepository notificationRepository, AuthRepository authRepository) {
        this.messagingTemplate = messagingTemplate;
        this.appointmentRepository = appointmentRepository;
        this.notificationRepository = notificationRepository;
        this.authRepository = authRepository;
    }

    @Override
    @Transactional
    public void markAsRead(UUID userId, UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId.toString()));

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new ForbiddenException("Acesso negado. Você não tem permissão para modificar esta notificação.");
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public List<NotificationDTO> getNotificationsForUser(UUID userId) {
        User recipient = authRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId.toString()));

        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient);

        return notifications.stream()
                .map(this::buildNotificationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void notifyPatientOfAppointment(UUID appointmentId) {
        Appointment appointment = appointmentRepository
                .findFirstById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", "id", appointmentId.toString()));

        Patient patient = appointment.getPatient();
        Nutritionist nutritionist = appointment.getSchedule().getLocation().getNutritionist();

        Notification notificationEntity = new Notification();
        notificationEntity.setRecipient(patient.getUser());
        notificationEntity.setSender(nutritionist.getUser()); // <--- SALVANDO O REMETENTE
        notificationEntity.setMessage("O nutricionista " + nutritionist.getUser().getFirstName() + " agendou uma nova consulta para você.");
        notificationEntity.setRelatedEntityId(appointment.getId());

        Notification savedNotification = notificationRepository.save(notificationEntity);

        NotificationDTO notificationPayload = buildNotificationDTO(savedNotification, patient, nutritionist); // Crie um método para montar o DTO

        messagingTemplate.convertAndSend("/topic/notifications/" + patient.getId(), notificationPayload);
    }

    private NotificationDTO buildNotificationDTO(Notification notificationEntity, Patient patient, Nutritionist nutritionist) {
        NotificationNutritionistDTO nutritionistDTO = new NotificationNutritionistDTO(
                nutritionist.getId(),
                nutritionist.getUser().getEmail(),
                nutritionist.getUser().getFirstName() + " " + nutritionist.getUser().getLastName()
        );

        NotificationPatientDTO patientDTO = new NotificationPatientDTO(
                patient.getId(),
                patient.getUser().getEmail(),
                patient.getUser().getFirstName() + " " + patient.getUser().getLastName()
        );

        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setId(notificationEntity.getId());
        notificationDTO.setFrom(nutritionistDTO);
        notificationDTO.setTo(patientDTO);
        notificationDTO.setMessage(notificationDTO.getMessage());
        notificationDTO.setRead(notificationEntity.isRead());
        notificationDTO.setRelatedEntityId(notificationEntity.getRelatedEntityId());
        notificationDTO.setCreatedAt(notificationEntity.getCreatedAt());

        return notificationDTO;
    }

    private NotificationDTO buildNotificationDTO(Notification notification) {
        NotificationNutritionistDTO fromDTO = null;
        if (notification.getSender() != null) {
            User sender = notification.getSender();
            fromDTO = new NotificationNutritionistDTO(
                    sender.getId(), // Este é o ID do User, não do Patient/Nutritionist
                    sender.getEmail(),
                    sender.getFirstName() + " " + sender.getLastName()
            );
        }

        User recipient = notification.getRecipient();
        NotificationPatientDTO toDTO = new NotificationPatientDTO(
                recipient.getId(),
                recipient.getEmail(),
                recipient.getFirstName() + " " + recipient.getLastName()
        );

        return new NotificationDTO(
                notification.getId(),
                fromDTO,
                toDTO,
                notification.getMessage(),
                notification.isRead(),
                notification.getRelatedEntityId(),
                notification.getCreatedAt()
        );
    }
}

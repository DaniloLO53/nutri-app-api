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
        // 1. Busca a notificação no banco de dados pelo seu ID.
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId.toString()));

        // 2. Verificação de Segurança (CRÍTICO): Confirma se o ID do usuário logado
        //    é o mesmo do ID do destinatário da notificação.
        if (!notification.getRecipient().getId().equals(userId)) {
            throw new ForbiddenException("Acesso negado. Você não tem permissão para modificar esta notificação.");
        }

        // 3. Altera o status para 'lida' e salva no banco.
        //    (Opcional: verificar se já está lida para evitar uma escrita desnecessária no banco)
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
                .map(notification -> buildNotificationDTO(notification)) // Mapeia cada notificação usando o método helper
                .collect(Collectors.toList());
    }

    @Override
    public void notifyPatientOfAppointment(UUID appointmentId) {
        Appointment appointment = appointmentRepository
                .findFirstById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", "id", appointmentId.toString()));

        Patient patient = appointment.getPatient();
        Nutritionist nutritionist = appointment.getSchedule().getLocation().getNutritionist();

        // 1. Persistir a notificação no banco de dados
        Notification notificationEntity = new Notification();
        notificationEntity.setRecipient(patient.getUser());
        notificationEntity.setSender(nutritionist.getUser()); // <--- SALVANDO O REMETENTE
        notificationEntity.setMessage("O nutricionista " + nutritionist.getUser().getFirstName() + " agendou uma nova consulta para você.");
        notificationEntity.setRelatedEntityId(appointment.getId());
        // 'isRead' e 'createdAt' terão valores padrão

        Notification savedNotification = notificationRepository.save(notificationEntity);

        // 2. Enviar a notificação em tempo real via WebSocket
        // É uma boa prática enviar o objeto salvo, pois ele agora tem um ID e data de criação.
        NotificationDTO notificationPayload = buildNotificationDTO(savedNotification, patient, nutritionist); // Crie um método para montar o DTO

        // O destino deve ser o ID do *paciente*
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

        // Constrói o DTO do destinatário
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

package org.nutri.app.nutri_app_api.services.notificationService;

import org.nutri.app.nutri_app_api.exceptions.ResourceNotFoundException;
import org.nutri.app.nutri_app_api.models.appointments.Appointment;
import org.nutri.app.nutri_app_api.models.locations.Location;
import org.nutri.app.nutri_app_api.models.notifications.Notification;
import org.nutri.app.nutri_app_api.payloads.notificationDTOs.NotificationDTO;
import org.nutri.app.nutri_app_api.payloads.notificationDTOs.NotificationNutritionistDTO;
import org.nutri.app.nutri_app_api.payloads.notificationDTOs.NotificationPatientDTO;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentRepository;
import org.nutri.app.nutri_app_api.repositories.notificationRepository.NotificationRepository;
import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;
import org.nutri.app.nutri_app_api.security.models.users.Patient;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final AppointmentRepository appointmentRepository;
    private final NotificationRepository notificationRepository;

    public NotificationServiceImpl(SimpMessagingTemplate messagingTemplate, AppointmentRepository appointmentRepository, NotificationRepository notificationRepository) {
        this.messagingTemplate = messagingTemplate;
        this.appointmentRepository = appointmentRepository;
        this.notificationRepository = notificationRepository;
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
        notificationEntity.setRecipient(patient.getUser()); // O destinatário é o usuário do paciente
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
}

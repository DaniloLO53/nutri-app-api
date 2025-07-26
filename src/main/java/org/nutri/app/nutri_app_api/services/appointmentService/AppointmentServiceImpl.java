package org.nutri.app.nutri_app_api.services.appointmentService;

import org.nutri.app.nutri_app_api.exceptions.ConflictException;
import org.nutri.app.nutri_app_api.exceptions.ForbiddenException;
import org.nutri.app.nutri_app_api.exceptions.ResourceNotFoundException;
import org.nutri.app.nutri_app_api.models.appointments.Appointment;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatus;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatusName;
import org.nutri.app.nutri_app_api.models.schedules.Schedule;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.*;
import org.nutri.app.nutri_app_api.payloads.locationDTOs.OwnLocationResponse;
import org.nutri.app.nutri_app_api.payloads.notificationDTOs.NotificationDTO;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.AppointmentOrSchedule;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.OwnScheduleDTO;
import org.nutri.app.nutri_app_api.repositories.AppointmentStatusRepository;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentNutritionistProjection;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentPatientProjection;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentRepository;
import org.nutri.app.nutri_app_api.repositories.patientRepository.PatientRepository;
import org.nutri.app.nutri_app_api.repositories.scheduleRepository.ScheduleRepository;
import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;
import org.nutri.app.nutri_app_api.security.models.users.Patient;
import org.nutri.app.nutri_app_api.security.models.users.RoleName;
import org.nutri.app.nutri_app_api.security.models.users.User;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.nutri.app.nutri_app_api.services.notificationService.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final static String DEFAULT_STATUS_NAME = AppointmentStatusName.AGENDADO.name();

    private final AppointmentRepository appointmentRepository;
    private final AppointmentStatusRepository appointmentStatusRepository;
    private final PatientRepository patientRepository;
    private final ScheduleRepository scheduleRepository;
    private final NotificationService notificationService;

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            AppointmentStatusRepository appointmentStatusRepository,
            PatientRepository patientRepository,
            ScheduleRepository scheduleRepository,
            NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentStatusRepository = appointmentStatusRepository;
        this.patientRepository = patientRepository;
        this.scheduleRepository = scheduleRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public OwnScheduleDTO deleteCanceledAppointment(UUID userId, UUID appointmentId) {
        Appointment appointment = appointmentRepository
                .findFirstById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", "id", appointmentId.toString()));

        Schedule schedule = appointment.getSchedule();
        authorizeAppointmentAction(userId, appointment);

        // Desvincular as entidades antes de deletar.
        // Remove a referência do schedule para o appointment que será deletado.
        if (schedule != null) {
            schedule.setAppointment(null);
        }
        appointmentRepository.delete(appointment);

        return createOwnScheduleDTO(schedule);
    }

    @Override
    @Transactional
    public ResponseToCreateAppointment cancelAppointmentByNutritionist(UUID userId, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", "id", appointmentId.toString()));

        authorizeAppointmentAction(userId, appointment);

        AppointmentStatus canceledStatus = appointmentStatusRepository.findFirstByName(AppointmentStatusName.CANCELADO.toString())
                .orElseThrow(() -> new ResourceNotFoundException("Status", "nome", AppointmentStatusName.CANCELADO.toString()));

        appointment.setAppointmentStatus(canceledStatus);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return toResponseDTO(updatedAppointment);
    }

    @Override
    @Transactional
    public ResponseToCreateAppointment cancelAppointmentByPatient(UUID userId, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", "id", appointmentId.toString()));

        authorizeAppointmentAction(userId, appointment);

        AppointmentStatus canceledStatus = appointmentStatusRepository.findFirstByName(AppointmentStatusName.CANCELADO.toString())
                .orElseThrow(() -> new ResourceNotFoundException("Status", "nome", AppointmentStatusName.CANCELADO.toString()));

        appointment.setAppointmentStatus(canceledStatus);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return toResponseDTO(updatedAppointment);
    }

    @Override
    @Transactional
    public ResponseToCreateAppointment requestAppointmentConfirmation(UUID userId, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", "id", appointmentId.toString()));

        authorizeAppointmentAction(userId, appointment);

        AppointmentStatus waitingConfirmationStatus = appointmentStatusRepository.findFirstByName(AppointmentStatusName.ESPERANDO_CONFIRMACAO.toString())
                .orElseThrow(() -> new ResourceNotFoundException("Status", "nome", AppointmentStatusName.ESPERANDO_CONFIRMACAO.toString()));

        appointment.setAppointmentStatus(waitingConfirmationStatus);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return toResponseDTO(updatedAppointment);
    }

    @Override
    @Transactional
    public ResponseToCreateAppointment confirmAppointment(UUID userId, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", "id", appointmentId.toString()));

        authorizeAppointmentAction(userId, appointment);

        AppointmentStatus waitingConfirmationStatus = appointmentStatusRepository.findFirstByName(AppointmentStatusName.CONFIRMADO.toString())
                .orElseThrow(() -> new ResourceNotFoundException("Status", "nome", AppointmentStatusName.CONFIRMADO.toString()));

        appointment.setAppointmentStatus(waitingConfirmationStatus);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return toResponseDTO(updatedAppointment);
    }

    @Override
    @Transactional
    public ResponseToCreateAppointment createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO) {
        UUID patientId = createAppointmentDTO.getPatientId();

        Schedule schedule = scheduleRepository.findById(createAppointmentDTO.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade", "id", createAppointmentDTO.getScheduleId().toString()));

        Patient patient = findPatient(userDetails, patientId);


        Nutritionist scheduleOwner = schedule.getLocation().getNutritionist();
        validateAppointmentCreation(userDetails, patient, schedule, scheduleOwner);

        AppointmentStatus defaultStatus = appointmentStatusRepository.findFirstByName(DEFAULT_STATUS_NAME)
                .orElseThrow(() -> new ResourceNotFoundException("Status", "nome", DEFAULT_STATUS_NAME));

        Appointment newAppointment = new Appointment();
        newAppointment.setSchedule(schedule);
        newAppointment.setPatient(patient);
        newAppointment.setAppointmentStatus(defaultStatus);
        newAppointment.setIsRemote(createAppointmentDTO.getIsRemote());

        Appointment savedAppointment = appointmentRepository.save(newAppointment);

        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        if (role.equals(RoleName.ROLE_NUTRITIONIST.name())) {
            notificationService.notifyPatientOfAppointment(savedAppointment.getId());
        }

        return toResponseDTO(savedAppointment);
    }

    @Override
    public Page<PatientAppointmentResponse> getPatientAppointments(UUID userId, Pageable pageable) {
        Page<AppointmentPatientProjection> projectionPage = appointmentRepository.getPatientAppointments(userId, pageable);

        List<PatientAppointmentResponse> dtos = projectionPage.stream().map(projection -> {
            PatientAppointmentResponse patientAppointmentResponse = new PatientAppointmentResponse();

            patientAppointmentResponse.setId(projection.getId().toString());
            patientAppointmentResponse.setIsRemote(projection.getIsRemote());

            OwnLocationResponse location = new OwnLocationResponse();
            location.setId(projection.getLocationId());
            location.setAddress(projection.getAddress());
            patientAppointmentResponse.setLocation(location);

            patientAppointmentResponse.setStartTime(projection.getStartTime());
            patientAppointmentResponse.setDurationMinutes(projection.getDurationMinutes());
            patientAppointmentResponse.setStatus(AppointmentStatusName.valueOf(projection.getStatus()));
            patientAppointmentResponse.setType(EventType.APPOINTMENT);

            PatientAppointmentResponseNutritionistDTO patientAppointmentResponseNutritionistDTO = new PatientAppointmentResponseNutritionistDTO();
            patientAppointmentResponseNutritionistDTO.setId(projection.getNutritionistId());
            patientAppointmentResponseNutritionistDTO.setName(projection.getNutritionistName());
            patientAppointmentResponseNutritionistDTO.setEmail(projection.getNutritionistEmail());

            patientAppointmentResponse.setNutritionist(patientAppointmentResponseNutritionistDTO);

            return patientAppointmentResponse;
        }).collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, projectionPage.getTotalElements());
    }

    @Override
    public Page<NutritionistFutureAppointmentDTO> getNutritionistFutureAppointments(UUID userId, Pageable pageable) {
        // ✅ 1. O repositório agora retorna um Page<Projection>
        Page<AppointmentNutritionistProjection> projectionPage = appointmentRepository.findNutritionistFutureAppointments(userId, pageable);

        // ✅ 2. Mapeia o CONTEÚDO da página de projeções para a lista de DTOs
        List<NutritionistFutureAppointmentDTO> dtos = projectionPage.getContent().stream().map(projection -> {
            NutritionistFutureAppointmentDTO dto = new NutritionistFutureAppointmentDTO();
            // ... (sua lógica de mapeamento permanece a mesma) ...
            dto.setId(projection.getId().toString());
            dto.setIsRemote(projection.getIsRemote());
            dto.setAddress(projection.getAddress());
            dto.setStartTime(projection.getStartTime());
            dto.setDurationMinutes(projection.getDurationMinutes());
            dto.setStatus(AppointmentStatusName.valueOf(projection.getStatus()));
            dto.setType(EventType.APPOINTMENT);

            NutritionistAppointmentResponsePatientDTO patientDTO = new NutritionistAppointmentResponsePatientDTO();
            patientDTO.setId(projection.getPatientId());
            patientDTO.setName(projection.getPatientName());
            patientDTO.setEmail(projection.getPatientEmail());
            dto.setPatient(patientDTO);

            return dto;
        }).collect(Collectors.toList());

        // ✅ 3. Cria e retorna uma nova Page com os DTOs mapeados e os metadados da página original
        return new PageImpl<>(dtos, pageable, projectionPage.getTotalElements());
    }

    private OwnScheduleDTO createOwnScheduleDTO(Schedule savedSchedule) {
        OwnScheduleDTO dto = new OwnScheduleDTO();

        dto.setId(savedSchedule.getId());
        dto.setStartTime(savedSchedule.getStartTime());
        dto.setDurationMinutes(savedSchedule.getDurationMinutes());
        dto.setType(AppointmentOrSchedule.SCHEDULE);
        dto.setStatus(AppointmentStatusName.AGENDADO);

        return dto;
    }

    private void authorizeAppointmentAction(UUID loggedInUserId, Appointment appointment) {
        UUID patientUserId = appointment.getPatient().getUser().getId();

        UUID nutritionistUserId = appointment.getSchedule().getLocation().getNutritionist().getUser().getId();

        if (!loggedInUserId.equals(patientUserId) && !loggedInUserId.equals(nutritionistUserId)) {
            throw new ForbiddenException("Você não tem permissão para modificar esta consulta.");
        }
    }

    private Patient findPatient(UserDetailsImpl userDetails, UUID patientIdFromDTO) {
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(RoleName.ROLE_NUTRITIONIST.name()))) {
            return patientRepository.findById(patientIdFromDTO)
                    .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id", patientIdFromDTO.toString()));
        } else {
            return patientRepository.findFirstByUser_Id(userDetails.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id de usuário", userDetails.getId().toString()));
        }
    }

    private void validateAppointmentCreation(UserDetailsImpl userDetails, Patient patient, Schedule schedule, Nutritionist scheduleOwner) {
        String userRole = userDetails.getAuthorities().iterator().next().getAuthority();
        UUID loggedInUserId = userDetails.getId();

        if (userRole.equals(RoleName.ROLE_PATIENT.name()) && !patient.getUser().getId().equals(loggedInUserId)) {
            throw new ForbiddenException("Pacientes só podem criar consultas para si mesmos.");
        }

        if (userRole.equals(RoleName.ROLE_NUTRITIONIST.name()) && !scheduleOwner.getUser().getId().equals(loggedInUserId)) {
            throw new ForbiddenException("Nutricionistas só podem criar consultas em sua própria agenda.");
        }

        if (schedule.getAppointment() != null) {
            throw new ConflictException("Este horário já está agendado.");
        }

        Optional<Appointment> optionalExistingAppointment = appointmentRepository
                .findFirstScheduledOrConfirmedByPatient(patient.getId());
        if (optionalExistingAppointment.isPresent()) {
            Appointment appointment = optionalExistingAppointment.get();
            LocalDateTime startTime = appointment.getSchedule().getStartTime();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'do dia' dd-MM-yyyy");

            String errorMessage = String.format(
                    "Paciente já possui consulta marcada ou agendada para %s",
                    startTime.format(formatter)
            );

            throw new ConflictException(errorMessage);
        }
    }

    private ResponseToCreateAppointment toResponseDTO(Appointment appointment) {
        ResponseToCreateAppointment dto = new ResponseToCreateAppointment();
        dto.setId(appointment.getId());
        dto.setType(AppointmentOrSchedule.APPOINTMENT);
        dto.setStatus(AppointmentStatusName.valueOf(appointment.getAppointmentStatus().getName()));
        dto.setDurationMinutes(appointment.getSchedule().getDurationMinutes());
        dto.setStartTime(appointment.getSchedule().getStartTime());

        PatientSearchByNameDTO patientDTO = new PatientSearchByNameDTO();
        User patientUser = appointment.getPatient().getUser();
        patientDTO.setId(appointment.getPatient().getId());
        patientDTO.setName(patientUser.getFirstName() + " " + patientUser.getLastName());
        patientDTO.setEmail(patientUser.getEmail());

        dto.setPatient(patientDTO);

        return dto;
    }
}

package org.nutri.app.nutri_app_api.services.appointmentService;

import org.nutri.app.nutri_app_api.exceptions.ConflictException;
import org.nutri.app.nutri_app_api.exceptions.ForbiddenException;
import org.nutri.app.nutri_app_api.exceptions.ResourceNotFoundException;
import org.nutri.app.nutri_app_api.models.appointments.Appointment;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatus;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatusName;
import org.nutri.app.nutri_app_api.models.schedules.Schedule;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.*;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public AppointmentServiceImpl(
            AppointmentRepository appointmentRepository,
            AppointmentStatusRepository appointmentStatusRepository,
            PatientRepository patientRepository,
            ScheduleRepository scheduleRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentStatusRepository = appointmentStatusRepository;
        this.patientRepository = patientRepository;
        this.scheduleRepository = scheduleRepository;
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
    public ResponseToCreateAppointment cancelAppointment(UUID userId, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Consulta", "id", appointmentId.toString()));

        authorizeAppointmentAction(userId, appointment);

        AppointmentStatus canceledStatus = appointmentStatusRepository.findFirstByName(AppointmentStatusName.CANCELADO.name())
                .orElseThrow(() -> new ResourceNotFoundException("Status", "nome", AppointmentStatusName.CANCELADO.name()));

        appointment.setAppointmentStatus(canceledStatus);

        Appointment updatedAppointment = appointmentRepository.save(appointment);

        return toResponseDTO(updatedAppointment);
    }

    @Override
    @Transactional
    public ResponseToCreateAppointment createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO) {
        Schedule schedule = scheduleRepository.findById(createAppointmentDTO.getScheduleId())
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidade", "id", createAppointmentDTO.getScheduleId().toString()));

        Patient patient = findPatient(userDetails, createAppointmentDTO.getPatientId());

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

        return toResponseDTO(savedAppointment);
    }

    @Override
    public Set<PatientFutureAppointmentDTO> getPatientFutureAppointments(UUID userId) {
        Set<AppointmentPatientProjection> projections = appointmentRepository.findPatientFutureAppointments(userId);

        return projections.stream().map(projection -> {
            PatientFutureAppointmentDTO patientFutureAppointmentDTO = new PatientFutureAppointmentDTO();

            patientFutureAppointmentDTO.setId(projection.getId().toString());
            patientFutureAppointmentDTO.setIsRemote(projection.getIsRemote());
            patientFutureAppointmentDTO.setAddress(projection.getAddress());
            patientFutureAppointmentDTO.setStartTime(projection.getStartTime());
            patientFutureAppointmentDTO.setDurationMinutes(projection.getDurationMinutes());
            patientFutureAppointmentDTO.setStatus(AppointmentStatusName.valueOf(projection.getStatus()));
            patientFutureAppointmentDTO.setType(EventType.APPOINTMENT);

            PatientFutureAppointmentNutritionistDTO patientFutureAppointmentNutritionistDTO = new PatientFutureAppointmentNutritionistDTO();
            patientFutureAppointmentNutritionistDTO.setId(projection.getNutritionistId());
            patientFutureAppointmentNutritionistDTO.setName(projection.getNutritionistName());
            patientFutureAppointmentNutritionistDTO.setEmail(projection.getNutritionistEmail());

            patientFutureAppointmentDTO.setNutritionist(patientFutureAppointmentNutritionistDTO);

            return patientFutureAppointmentDTO;
        }).collect(Collectors.toSet());
    }

    @Override
    public Set<NutritionistFutureAppointmentDTO> getNutritionistFutureAppointments(UUID userId) {
        Set<AppointmentNutritionistProjection> projections = appointmentRepository.findNutritionistFutureAppointments(userId);

        return projections.stream().map(projection -> {
            NutritionistFutureAppointmentDTO nutritionistFutureAppointmentDTO = new NutritionistFutureAppointmentDTO();

            nutritionistFutureAppointmentDTO.setId(projection.getId().toString());
            nutritionistFutureAppointmentDTO.setIsRemote(projection.getIsRemote());
            nutritionistFutureAppointmentDTO.setAddress(projection.getAddress());
            nutritionistFutureAppointmentDTO.setStartTime(projection.getStartTime());
            nutritionistFutureAppointmentDTO.setDurationMinutes(projection.getDurationMinutes());
            nutritionistFutureAppointmentDTO.setStatus(AppointmentStatusName.valueOf(projection.getStatus()));
            nutritionistFutureAppointmentDTO.setType(EventType.APPOINTMENT);

            NutritionistFutureAppointmentPatientDTO nutritionistFutureAppointmentPatientDTO = new NutritionistFutureAppointmentPatientDTO();
            nutritionistFutureAppointmentPatientDTO.setId(projection.getPatientId());
            nutritionistFutureAppointmentPatientDTO.setName(projection.getPatientName());
            nutritionistFutureAppointmentPatientDTO.setEmail(projection.getPatientEmail());

            nutritionistFutureAppointmentDTO.setPatient(nutritionistFutureAppointmentPatientDTO);

            return nutritionistFutureAppointmentDTO;
        }).collect(Collectors.toSet());
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

        boolean patientHasAppointment = appointmentRepository.patientAlreadyHasSchedule(
                patient.getId(),
                AppointmentStatusName.AGENDADO.name(),
                AppointmentStatusName.CONFIRMADO.name(),
                schedule.getStartTime()
        );
        if (patientHasAppointment) {
            throw new ConflictException("Este paciente já possui uma consulta neste mesmo dia e horário.");
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

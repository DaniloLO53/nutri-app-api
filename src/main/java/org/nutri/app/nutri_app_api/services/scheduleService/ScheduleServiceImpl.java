package org.nutri.app.nutri_app_api.services.scheduleService;

import org.nutri.app.nutri_app_api.exceptions.ConflictException;
import org.nutri.app.nutri_app_api.exceptions.ForbiddenException;
import org.nutri.app.nutri_app_api.exceptions.ResourceNotFoundException;
import org.nutri.app.nutri_app_api.models.appointments.Appointment;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatus;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatusName;
import org.nutri.app.nutri_app_api.models.locations.Location;
import org.nutri.app.nutri_app_api.models.schedules.Schedule;
import org.nutri.app.nutri_app_api.payloads.locationDTOs.OwnLocationResponse;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.*;
import org.nutri.app.nutri_app_api.repositories.AppointmentStatusRepository;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentRepository;
import org.nutri.app.nutri_app_api.repositories.locationRepository.LocationRepository;
import org.nutri.app.nutri_app_api.repositories.nutritionistRepository.NutritionistRepository;
import org.nutri.app.nutri_app_api.repositories.patientRepository.PatientRepository;
import org.nutri.app.nutri_app_api.repositories.scheduleRepository.OwnScheduleProjection;
import org.nutri.app.nutri_app_api.repositories.scheduleRepository.ScheduleRepository;
import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;
import org.nutri.app.nutri_app_api.security.models.users.Patient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final NutritionistRepository nutritionistRepository;
    private final PatientRepository patientRepository;
    private final LocationRepository locationRepository;
    private final AppointmentRepository appointmentRepository;
    private final AppointmentStatusRepository appointmentStatusRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository,
                               NutritionistRepository nutritionistRepository,
                               PatientRepository patientRepository,
                               LocationRepository locationRepository,
                               AppointmentRepository appointmentRepository,
                               AppointmentStatusRepository appointmentStatusRepository) {
        this.scheduleRepository = scheduleRepository;
        this.nutritionistRepository = nutritionistRepository;
        this.patientRepository = patientRepository;
        this.locationRepository = locationRepository;
        this.appointmentRepository = appointmentRepository;
        this.appointmentStatusRepository = appointmentStatusRepository;
    }

    @Override
    public void deleteSchedule(UUID userId, UUID scheduleId) {
        Schedule scheduleToDelete = scheduleRepository
                .findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Horário", "id", scheduleId.toString()));

        UUID ownerUserId = scheduleToDelete.getLocation().getNutritionist().getUser().getId();

        if (!ownerUserId.equals(userId)) {
            throw new ConflictException("Você não tem permissão para deletar este horário.");
        }

        scheduleRepository.delete(scheduleToDelete);
    }

    @Override
    public Set<OwnScheduleDTO> getOwnSchedules(UUID userId, ScheduleParameters params) {
        LocalDateTime startTime = params.getStartDate().atStartOfDay();
        LocalDateTime endTime = params.getEndDate().plusDays(1).atStartOfDay();

        Nutritionist nutritionist = nutritionistRepository
                .findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId.toString()));

        Set<OwnScheduleProjection> schedulesByStartAndEndDate = scheduleRepository
                .findOwnSchedulesByStartAndEndDate(nutritionist.getId(), startTime, endTime);

        return schedulesByStartAndEndDate.stream().map(this::convertProjectionToDto).collect(Collectors.toSet());
    }

    @Override
    public Set<OwnScheduleDTO> getSchedulesFromNutritionist(UUID userId, UUID nutritionistId, ScheduleParameters params) {
        LocalDateTime startTime = params.getStartDate().atStartOfDay();
        LocalDateTime endTime = params.getEndDate().plusDays(1).atStartOfDay();

        Patient patient = patientRepository
                .findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente", "id de usuário", userId.toString()));

        Nutritionist nutritionist = nutritionistRepository
                .findFirstById(nutritionistId)
                .orElseThrow(() -> new ResourceNotFoundException("Nutricionista", "id", nutritionistId.toString()));

        Set<OwnScheduleProjection> schedulesByStartAndEndDate = scheduleRepository
                .findOwnSchedulesByStartAndEndDate(nutritionist.getId(), startTime, endTime);

        Set<OwnScheduleDTO> scheduleDTO = schedulesByStartAndEndDate
                .stream()
                .map(this::convertProjectionToDto).collect(Collectors.toSet());

        return scheduleDTO.stream().peek(dto -> {
            boolean isAppointment = dto.getType().equals(AppointmentOrSchedule.APPOINTMENT);
            boolean isOwnAppointment = patient.getId().equals(dto.getPatient().getId());

            if (isAppointment && !isOwnAppointment) {
                dto.setPatient(null);
            }
        }).collect(Collectors.toSet());
    }

    @Override
    public OwnScheduleDTO createSchedule(UUID userId, UUID locationId, ScheduleCreateDTO scheduleDTO) {
        Nutritionist nutritionist = nutritionistRepository.findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Nutricionista", "id", userId.toString()));

        Location location = locationRepository
                .findById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Localidade", "id", locationId.toString()));

        if (!location.getNutritionist().getId().equals(nutritionist.getId())) {
            throw new ConflictException("Este local de atendimento não pertence ao nutricionista logado.");
        }

        CustomLocalDateTime startLocalDateTime = scheduleDTO.getStartLocalDateTime();
        LocalDateTime scheduleStart = LocalDateTime.of(
                startLocalDateTime.getYear(),
                startLocalDateTime.getMonth(),
                startLocalDateTime.getDay(),
                startLocalDateTime.getHour(),
                startLocalDateTime.getMinute());
        LocalDateTime scheduleEnd = scheduleStart.plusMinutes(scheduleDTO.getDurationMinutes());

        if (scheduleStart.isBefore(LocalDateTime.now())) {
            throw new ConflictException("Não é possível criar horários no passado.");
        }

        if (scheduleRepository.existsOverlappingSchedule(nutritionist.getId(), scheduleStart, scheduleEnd)) {
            throw new ConflictException("Este horário sobrepõe um horário de disponibilidade ou consulta já existente.");
        }

        Schedule schedule = new Schedule();
        schedule.setDurationMinutes(scheduleDTO.getDurationMinutes());
        schedule.setStartTime(scheduleStart);
        schedule.setLocation(location); // Apenas associe a localização

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return createOwnScheduleDTO(savedSchedule);
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

    private OwnScheduleDTO convertProjectionToDto(OwnScheduleProjection projection) {
        if (projection == null) {
            return null;
        }

        // 1. Lógica para decidir qual ID usar
        UUID id = projection.getAppointmentId() != null ? projection.getAppointmentId() : projection.getScheduleId();

        // 2. Mapeamento direto
        LocalDateTime startTime = projection.getStartTime();
        Integer durationMinutes = projection.getDurationMinutes();

        // 3. Conversão de String para Enum
        AppointmentOrSchedule type = AppointmentOrSchedule.valueOf(projection.getType());

        // 4. Tratamento de campos que podem ser nulos na projeção
        // Para satisfazer o @NotNull do DTO, fornecemos valores padrão.
        String patientName = projection.getPatientName() != null ? projection.getPatientName() : "Vaga Disponível";

        AppointmentStatusName status = projection.getStatus() != null
                ? AppointmentStatusName.valueOf(projection.getStatus())
                : AppointmentStatusName.DISPONIVEL;

        PatientSearchByNameDTO patientDTO = new PatientSearchByNameDTO();
        patientDTO.setName(patientName);
        patientDTO.setEmail(projection.getPatientEmail());
        patientDTO.setId(projection.getPatientId());

        OwnLocationResponse location = new OwnLocationResponse();
        location.setId(projection.getLocationId().toString());
        location.setAddress(projection.getAddress());

        // 5. Criação do DTO
        return new OwnScheduleDTO(id, startTime, durationMinutes, type, patientDTO, status, location);
    }
}

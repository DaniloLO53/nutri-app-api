package org.nutri.app.nutri_app_api.services.scheduleService;

import org.nutri.app.nutri_app_api.exceptions.ConflictException;
import org.nutri.app.nutri_app_api.exceptions.ResourceNotFoundException;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatusName;
import org.nutri.app.nutri_app_api.models.locations.Location;
import org.nutri.app.nutri_app_api.models.schedules.Schedule;
import org.nutri.app.nutri_app_api.payloads.locationDTOs.OwnLocationResponse;
import org.nutri.app.nutri_app_api.payloads.patientDTOs.PatientSearchByNameDTO;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.AppointmentOrSchedule;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.CustomLocalDateTime;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.OwnScheduleDTO;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.ScheduleCreateDTO;
import org.nutri.app.nutri_app_api.repositories.locationRepository.LocationRepository;
import org.nutri.app.nutri_app_api.repositories.nutritionistRepository.NutritionistRepository;
import org.nutri.app.nutri_app_api.repositories.scheduleRepository.OwnScheduleProjection;
import org.nutri.app.nutri_app_api.repositories.scheduleRepository.ScheduleRepository;
import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;
import org.nutri.app.nutri_app_api.security.models.users.Patient;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final NutritionistRepository nutritionistRepository;
    private final LocationRepository locationRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository,
                               NutritionistRepository nutritionistRepository,
                               LocationRepository locationRepository) {
        this.scheduleRepository = scheduleRepository;
        this.nutritionistRepository = nutritionistRepository;
        this.locationRepository = locationRepository;
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
    public Set<OwnScheduleDTO> getOwnSchedules(UserDetailsImpl userDetails, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.plusDays(1).atStartOfDay();

        Nutritionist nutritionist = (Nutritionist) userDetails.getEntityByRole();

        Set<OwnScheduleProjection> schedulesByStartAndEndDate = scheduleRepository
                .findOwnSchedulesByStartAndEndDate(nutritionist.getId(), startTime, endTime);

        return schedulesByStartAndEndDate.stream().map(this::convertProjectionToDto).collect(Collectors.toSet());
    }

    @Override
    public Set<OwnScheduleDTO> getSchedulesFromNutritionist(UserDetailsImpl userDetails, UUID nutritionistId, UUID locationId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startTime = startDate.atStartOfDay();
        LocalDateTime endTime = endDate.plusDays(1).atStartOfDay();

        Patient patient = (Patient) userDetails.getEntityByRole();

        if (!locationRepository.existsById(locationId)) {
            throw new ResourceNotFoundException("Localidade", "id", locationId.toString());
        }

        Nutritionist nutritionist = nutritionistRepository
                .findFirstById(nutritionistId)
                .orElseThrow(() -> new ResourceNotFoundException("Nutricionista", "id", nutritionistId.toString()));

        Set<OwnScheduleProjection> schedulesByStartAndEndDateAndLocation = scheduleRepository
                .findOwnSchedulesByStartAndEndDateAndLocation(nutritionist.getId(), locationId, startTime, endTime);

        Set<OwnScheduleDTO> scheduleDTO = schedulesByStartAndEndDateAndLocation
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
        schedule.setLocation(location);

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

        UUID id = projection.getAppointmentId() != null ? projection.getAppointmentId() : projection.getScheduleId();

        LocalDateTime startTime = projection.getStartTime();
        Integer durationMinutes = projection.getDurationMinutes();

        AppointmentOrSchedule type = AppointmentOrSchedule.valueOf(projection.getType());

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

        return new OwnScheduleDTO(id, startTime, durationMinutes, type, patientDTO, status, location);
    }
}

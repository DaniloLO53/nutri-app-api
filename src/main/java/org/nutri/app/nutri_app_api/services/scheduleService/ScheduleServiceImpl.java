package org.nutri.app.nutri_app_api.services.scheduleService;

import org.nutri.app.nutri_app_api.exceptions.ConflictException;
import org.nutri.app.nutri_app_api.exceptions.ResourceNotFoundException;
import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatusName;
import org.nutri.app.nutri_app_api.models.schedules.Schedule;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.*;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentRepository;
import org.nutri.app.nutri_app_api.repositories.scheduleRepository.OwnScheduleProjection;
import org.nutri.app.nutri_app_api.repositories.nutritionistRepository.ProfileRepository;
import org.nutri.app.nutri_app_api.repositories.scheduleRepository.ScheduleProjection;
import org.nutri.app.nutri_app_api.repositories.scheduleRepository.ScheduleRepository;
import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final ProfileRepository nutritionistRepository;
    private final AppointmentRepository appointmentRepository;

    public ScheduleServiceImpl(ScheduleRepository scheduleRepository, ProfileRepository nutritionistRepository, AppointmentRepository appointmentRepository) {
        this.scheduleRepository = scheduleRepository;
        this.nutritionistRepository = nutritionistRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public void deleteSchedule(UUID userId, UUID scheduleId) {
        Schedule schedule = scheduleRepository
                .findFirstById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Horário", "id", scheduleId.toString()));

        Nutritionist nutritionist = nutritionistRepository
                .findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId.toString()));

        nutritionist.getSchedules().remove(schedule);

        nutritionistRepository.save(nutritionist);
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
    public Set<ScheduleCreateDTO> getSchedulesFromNutritionist(UUID nutritionistId, ScheduleParameters params) {
        LocalDateTime startTime = params.getStartDate().atStartOfDay();
        LocalDateTime endTime = params.getEndDate().plusDays(1).atStartOfDay();

        nutritionistRepository
                .findById(nutritionistId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmacêutico", "id", nutritionistId.toString()));

        Set<ScheduleProjection> schedulesByStartAndEndDate = scheduleRepository
                .findSchedulesByStartAndEndDate(nutritionistId, startTime, endTime);

        return schedulesByStartAndEndDate.stream().map(
                avail -> {
                    LocalDateTime scheduleStartTime = avail.getStartTime();
                    Integer year = scheduleStartTime.getYear();
                    Integer month = scheduleStartTime.getMonthValue();
                    Integer day = scheduleStartTime.getDayOfMonth();
                    Integer hour = scheduleStartTime.getHour();
                    Integer minute = scheduleStartTime.getMinute();

                    CustomLocalDateTime customLocalDateTime = new CustomLocalDateTime(year, month, day, hour, minute);
                    Integer durationMinutes = avail.getDurationMinutes();

                    UUID id = avail.getId();

                    return new ScheduleCreateDTO(id, customLocalDateTime, durationMinutes);
                }
        ).collect(Collectors.toSet());
    }

    @Override
    public void createSchedule(UUID userId, ScheduleCreateDTO scheduleDTO) {
        Nutritionist nutritionist = nutritionistRepository.findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmacêutico", "id", userId.toString()));

        CustomLocalDateTime startLocalDateTime = scheduleDTO.getStartLocalDateTime();

        LocalDateTime scheduleStart = LocalDateTime.of(
                startLocalDateTime.getYear(),
                startLocalDateTime.getMonth(),
                startLocalDateTime.getDay(),
                startLocalDateTime.getHour(),
                startLocalDateTime.getMinute());
        LocalDateTime scheduleEnd = scheduleStart.plusMinutes(scheduleDTO.getDurationMinutes());

        if (verifyNutritionistHasOverlapSchedule(nutritionist, scheduleStart, scheduleEnd)) {
            throw new ConflictException("Schedule is already booked");
        }

        if (appointmentRepository.hasOverlappingAppointment(nutritionist.getId(), scheduleStart, scheduleEnd)) {
            throw new ConflictException("This time slot overlaps with an existing scheduled appointment.");
        }

        if (scheduleStart.isBefore(LocalDateTime.now())) {
            throw new ConflictException("Schedule can't be on past");
        }

        Schedule schedule = new Schedule();

        schedule.setDurationMinutes(scheduleDTO.getDurationMinutes());
        schedule.setStartTime(scheduleStart);

        nutritionist.getSchedules().add(schedule);
        schedule.setNutritionist(nutritionist);


        // It doesn't save a new nutritionist - if nutritionist has id, it updates. Otherwise, it creates a new one
        nutritionistRepository.save(nutritionist);
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
            : AppointmentStatusName.DISPONIVEL; // Supondo que você tenha um status 'DISPONIVEL' no enum

        // 5. Criação do DTO
        return new OwnScheduleDTO(id, startTime, durationMinutes, type, patientName, status);
    }

    private boolean verifyNutritionistHasOverlapSchedule(Nutritionist nutritionist,
                                                       LocalDateTime scheduleStart,
                                                       LocalDateTime scheduleEnd) {
        return nutritionist.getSchedules().stream().anyMatch(
                avail -> {
                    LocalDateTime start = avail.getStartTime();
                    Integer duration = avail.getDurationMinutes();
                    LocalDateTime end = start.plusMinutes(duration);

                    return (scheduleEnd.isAfter(start) && scheduleStart.isBefore(end));
                }
        );
    }
}

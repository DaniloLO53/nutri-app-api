package org.nutri.app.nutri_app_api.services.scheduleService;

import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.OwnScheduleDTO;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.ScheduleCreateDTO;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public interface ScheduleService {
    OwnScheduleDTO createSchedule(UUID userId, UUID locationId, ScheduleCreateDTO nutritionistScheduleDTO);
    Set<OwnScheduleDTO> getSchedulesFromNutritionist(UserDetailsImpl userDetails, UUID nutritionistId, UUID locationId, LocalDate startDate, LocalDate endDate);
    Set<OwnScheduleDTO> getOwnSchedules(UserDetailsImpl userDetails, LocalDate startDate, LocalDate endDate);
    void deleteSchedule(UUID userId, UUID scheduleId);
}

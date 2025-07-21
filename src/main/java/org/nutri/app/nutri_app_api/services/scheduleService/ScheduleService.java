package org.nutri.app.nutri_app_api.services.scheduleService;

import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.ScheduleCreateDTO;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.ScheduleParameters;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.OwnScheduleDTO;

import java.util.Set;
import java.util.UUID;

public interface ScheduleService {
    OwnScheduleDTO createSchedule(UUID userId, ScheduleCreateDTO nutritionistScheduleDTO);
    Set<ScheduleCreateDTO> getSchedulesFromNutritionist(UUID nutritionistId, ScheduleParameters params);
    Set<OwnScheduleDTO> getOwnSchedules(UUID userId, ScheduleParameters params);
    void deleteSchedule(UUID userId, UUID scheduleId);
}

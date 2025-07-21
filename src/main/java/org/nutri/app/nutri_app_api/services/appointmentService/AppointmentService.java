package org.nutri.app.nutri_app_api.services.appointmentService;

import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.AppointmentDTO;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.ResponseToCreateAppointment;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.OwnScheduleDTO;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentPatientProjection;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;

import java.util.Set;
import java.util.UUID;

public interface AppointmentService {
    ResponseToCreateAppointment createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO);
    Set<AppointmentPatientProjection> getPatientFutureAppointments(UUID userId);
    Set<AppointmentDTO> getNutritionistFutureAppointments(UUID userId);
    ResponseToCreateAppointment deleteAppointment(UUID userId, UUID appointmentId);
}

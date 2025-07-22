package org.nutri.app.nutri_app_api.services.appointmentService;

import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.NutritionistFutureAppointmentDTO;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.PatientFutureAppointmentDTO;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.ResponseToCreateAppointment;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentPatientProjection;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;

import java.util.Set;
import java.util.UUID;

public interface AppointmentService {
    ResponseToCreateAppointment createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO);
    Set<PatientFutureAppointmentDTO> getPatientFutureAppointments(UUID userId);
    Set<NutritionistFutureAppointmentDTO> getNutritionistFutureAppointments(UUID userId);
    ResponseToCreateAppointment deleteAppointment(UUID userId, UUID appointmentId);
}

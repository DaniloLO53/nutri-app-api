package org.nutri.app.nutri_app_api.services.appointmentService;

import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.NutritionistFutureAppointmentDTO;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.PatientAppointmentResponse;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.ResponseToCreateAppointment;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.OwnScheduleDTO;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;

import java.util.Set;
import java.util.UUID;

public interface AppointmentService {
    ResponseToCreateAppointment createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO);
    Set<PatientAppointmentResponse> getPatientAppointments(UUID userId);
    Set<NutritionistFutureAppointmentDTO> getNutritionistFutureAppointments(UUID userId);
    ResponseToCreateAppointment cancelAppointmentByNutritionist(UUID userId, UUID appointmentId);
    ResponseToCreateAppointment cancelAppointmentByPatient(UUID userId, UUID appointmentId);
    ResponseToCreateAppointment requestAppointmentConfirmation(UUID userId, UUID appointmentId);
    ResponseToCreateAppointment confirmAppointment(UUID userId, UUID appointmentId);
    OwnScheduleDTO deleteCanceledAppointment(UUID userId, UUID appointmentId);
}

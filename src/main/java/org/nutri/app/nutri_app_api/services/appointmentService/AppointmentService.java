package org.nutri.app.nutri_app_api.services.appointmentService;

import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.NutritionistFutureAppointmentDTO;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.PatientAppointmentResponse;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.ResponseToCreateAppointment;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.OwnScheduleDTO;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface AppointmentService {
    ResponseToCreateAppointment createAppointment(UserDetailsImpl userDetails, CreateAppointmentDTO createAppointmentDTO);
    Page<PatientAppointmentResponse> getPatientAppointments(UUID userId, Pageable pageable);
    Page<NutritionistFutureAppointmentDTO> getNutritionistFutureAppointments(UUID userId, Pageable pageable);
    ResponseToCreateAppointment cancelAppointmentByNutritionist(UUID userId, UUID appointmentId);
    ResponseToCreateAppointment cancelAppointmentByPatient(UUID userId, UUID appointmentId);
    ResponseToCreateAppointment requestAppointmentConfirmation(UUID userId, UUID appointmentId);
    ResponseToCreateAppointment confirmAppointment(UUID userId, UUID appointmentId);
    OwnScheduleDTO deleteCanceledAppointment(UUID userId, UUID appointmentId);
}

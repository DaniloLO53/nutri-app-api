package org.nutri.app.nutri_app_api.controllers;

import jakarta.validation.Valid;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.NutritionistFutureAppointmentDTO;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.PatientAppointmentResponse;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.ResponseToCreateAppointment;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.OwnScheduleDTO;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.nutri.app.nutri_app_api.services.appointmentService.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/appointments/schedules/{scheduleId}")
    @PreAuthorize("hasAnyRole('ROLE_PATIENT', 'ROLE_NUTRITIONIST')")
    public ResponseEntity<ResponseToCreateAppointment> createAppointment(
            Authentication authentication,
            @Valid @RequestBody CreateAppointmentDTO createAppointmentDTO,
            @PathVariable UUID scheduleId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        createAppointmentDTO.setScheduleId(scheduleId);

        ResponseToCreateAppointment appointment = appointmentService.createAppointment(userDetails, createAppointmentDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @GetMapping("/patients/me/appointments")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<Set<PatientAppointmentResponse>> getPatientAppointments(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Set<PatientAppointmentResponse> appointments = appointmentService.getPatientAppointments(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointments);
    }

    @GetMapping("/nutritionists/me/appointments")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<Set<NutritionistFutureAppointmentDTO>> getNutritionistFutureAppointments(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Set<NutritionistFutureAppointmentDTO> appointments = appointmentService.getNutritionistFutureAppointments(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointments);
    }

    @DeleteMapping("/nutritionists/me/appointments/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<OwnScheduleDTO> deleteCanceledAppointment(
            Authentication authentication,
            @PathVariable UUID appointmentId) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        OwnScheduleDTO savedScheduleDTO = appointmentService.deleteCanceledAppointment(userId, appointmentId);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedScheduleDTO);
    }

    @PostMapping("/nutritionists/me/appointments/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<ResponseToCreateAppointment> cancelAppointmentByNutritionist(Authentication authentication, @PathVariable UUID appointmentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        ResponseToCreateAppointment responseToCreateAppointment = appointmentService.cancelAppointmentByNutritionist(userId, appointmentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseToCreateAppointment);
    }

    @PostMapping("/patients/me/appointments/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<ResponseToCreateAppointment> cancelAppointmentByPatient(Authentication authentication, @PathVariable UUID appointmentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        ResponseToCreateAppointment responseToCreateAppointment = appointmentService.cancelAppointmentByPatient(userId, appointmentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseToCreateAppointment);
    }

    @PostMapping("/appointments/{appointmentId}/request-confirmation")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<ResponseToCreateAppointment> requestAppointmentConfirmation(Authentication authentication, @PathVariable UUID appointmentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        ResponseToCreateAppointment responseToCreateAppointment = appointmentService.requestAppointmentConfirmation(userId, appointmentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseToCreateAppointment);
    }

    @PostMapping("/appointments/{appointmentId}/confirm")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<ResponseToCreateAppointment> confirmAppointment(Authentication authentication, @PathVariable UUID appointmentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        ResponseToCreateAppointment responseToCreateAppointment = appointmentService.confirmAppointment(userId, appointmentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(responseToCreateAppointment);
    }
}

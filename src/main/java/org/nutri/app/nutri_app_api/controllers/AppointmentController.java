package org.nutri.app.nutri_app_api.controllers;

import jakarta.validation.Valid;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.AppointmentDTO;
import org.nutri.app.nutri_app_api.payloads.appointmentDTOs.CreateAppointmentDTO;
import org.nutri.app.nutri_app_api.repositories.appointmentRepository.AppointmentPatientProjection;
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
    public ResponseEntity<CreateAppointmentDTO> createAppointment(
            Authentication authentication,
            @Valid @RequestBody CreateAppointmentDTO createAppointmentDTO,
            @PathVariable UUID scheduleId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        createAppointmentDTO.setScheduleId(scheduleId);

        CreateAppointmentDTO appointment = appointmentService.createAppointment(userDetails, createAppointmentDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @GetMapping("/patients/me/appointments/future")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<Set<AppointmentPatientProjection>> getPatientFutureAppointments(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Set<AppointmentPatientProjection> appointments = appointmentService.getPatientFutureAppointments(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointments);
    }

    @GetMapping("/nutritionists/me/appointments/future")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<Set<AppointmentDTO>> getNutritionistFutureAppointments(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Set<AppointmentDTO> appointments = appointmentService.getNutritionistFutureAppointments(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(appointments);
    }

    @DeleteMapping("nutritionists/me/appointments/{appointmentId}")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<?> deleteAppointment(Authentication authentication, @PathVariable UUID appointmentId) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        appointmentService.deleteAppointment(userId, appointmentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}

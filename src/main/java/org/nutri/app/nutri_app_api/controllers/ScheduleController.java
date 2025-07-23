package org.nutri.app.nutri_app_api.controllers;

import jakarta.validation.Valid;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.ScheduleCreateDTO;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.ScheduleParameters;
import org.nutri.app.nutri_app_api.payloads.scheduleDTOs.OwnScheduleDTO;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.nutri.app.nutri_app_api.services.scheduleService.ScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ScheduleController {
    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/nutritionists/{nutritionist_id}/schedules")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<Set<OwnScheduleDTO>> getSchedulesFromNutritionist(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID nutritionist_id,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ScheduleParameters params = new ScheduleParameters
                .Builder()
                .withStartDate(startDate)
                .withEndDate(endDate)
                .build();

        UUID userId = userDetails.getId();
        Set<OwnScheduleDTO> schedules = scheduleService.getSchedulesFromNutritionist(userId, nutritionist_id, params);

        return ResponseEntity.status(HttpStatus.OK).body(schedules);
    }

    @GetMapping("/nutritionists/me/schedules")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<Set<OwnScheduleDTO>> getOwnSchedules(
            Authentication authentication,
            @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        ScheduleParameters params = new ScheduleParameters
                .Builder()
                .withStartDate(startDate)
                .withEndDate(endDate)
                .build();

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        Set<OwnScheduleDTO> schedules = scheduleService.getOwnSchedules(userId, params);

        return ResponseEntity.status(HttpStatus.OK).body(schedules);
    }

    @PostMapping("/nutritionists/me/schedules/{locationId}")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<OwnScheduleDTO> createSchedule(
            Authentication authentication,
            @PathVariable UUID locationId,
            @RequestBody @Valid ScheduleCreateDTO scheduleDTO) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        OwnScheduleDTO savedScheduleDTO = scheduleService.createSchedule(userId, locationId, scheduleDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedScheduleDTO);
    }

    @DeleteMapping("/nutritionists/me/schedules/{scheduleId}")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<?> deleteSchedule(Authentication authentication, @PathVariable UUID scheduleId) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        scheduleService.deleteSchedule(userId, scheduleId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}

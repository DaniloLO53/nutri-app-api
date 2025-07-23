package org.nutri.app.nutri_app_api.controllers;

import org.nutri.app.nutri_app_api.payloads.locationDTOs.OwnLocationResponse;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.nutri.app.nutri_app_api.services.locationService.LocationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping("/nutritionists/me/locations")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<Set<OwnLocationResponse>> getOwnLocations(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        Set<OwnLocationResponse> locations = locationService.getOwnLocations(userDetails.getId());

        return ResponseEntity.status(HttpStatus.OK).body(locations);
    }
}

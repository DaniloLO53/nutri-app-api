package org.nutri.app.nutri_app_api.controllers;

import jakarta.validation.Valid;
import org.nutri.app.nutri_app_api.payloads.nutritionistDTOs.NutritionistProfile;
import org.nutri.app.nutri_app_api.payloads.nutritionistDTOs.ProfileSearchParamsDTO;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.nutri.app.nutri_app_api.services.nutritionistService.NutritionistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class NutritionistController {
    private final NutritionistService nutritionistService;

    public NutritionistController(NutritionistService nutritionistService) {
        this.nutritionistService = nutritionistService;
    }

    @GetMapping("/nutritionists/search")
    @PreAuthorize("hasRole('ROLE_PATIENT')")
    public ResponseEntity<Set<ProfileSearchParamsDTO>> getProfiles(
            @RequestParam(name = "nutritionistName", required = false) String nutritionistName,
            @RequestParam(name = "ibgeApiCity", required = false) String ibgeApiCity,
            @RequestParam(name = "ibgeApiState", required = false) String ibgeApiState,
            @RequestParam(name = "acceptsRemote", required = false) String acceptsRemote) {

        ProfileSearchParamsDTO params = new ProfileSearchParamsDTO();

        params.setAcceptsRemote(Boolean.parseBoolean(acceptsRemote));
        params.setIbgeApiState(ibgeApiState);
        params.setIbgeApiCity(ibgeApiCity);
        params.setNutritionistName(nutritionistName);

        Set<ProfileSearchParamsDTO> profiles = nutritionistService.getProfilesByParams(params);

        return ResponseEntity.status(HttpStatus.OK).body(profiles);
    }

    @GetMapping("/nutritionists/me")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<NutritionistProfile> getNutritionistProfile(Authentication authentication) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        NutritionistProfile savedNutritionistProfile = nutritionistService.getNutritionistProfile(userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedNutritionistProfile);
    }

    @PutMapping("/nutritionists/me")
    @PreAuthorize("hasRole('ROLE_NUTRITIONIST')")
    public ResponseEntity<NutritionistProfile> updateNutritionistProfile(
            Authentication authentication,
            @RequestBody @Valid NutritionistProfile nutritionistProfile) {

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        UUID userId = userDetails.getId();

        NutritionistProfile savedNutritionistProfile = nutritionistService.updateNutritionistProfile(userId, nutritionistProfile);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedNutritionistProfile);
    }
}

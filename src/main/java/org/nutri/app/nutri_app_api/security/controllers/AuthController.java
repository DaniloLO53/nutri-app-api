package org.nutri.app.nutri_app_api.security.controllers;

import jakarta.validation.Valid;
import org.nutri.app.nutri_app_api.security.DTOs.RequestSignIn;
import org.nutri.app.nutri_app_api.security.DTOs.RequestSignUpNutritionist;
import org.nutri.app.nutri_app_api.security.DTOs.RequestSignUpPatient;
import org.nutri.app.nutri_app_api.security.DTOs.ResponseSignIn;
import org.nutri.app.nutri_app_api.security.models.users.RoleName;
import org.nutri.app.nutri_app_api.security.repositories.UserInfoProjection;
import org.nutri.app.nutri_app_api.security.services.AuthService;
import org.nutri.app.nutri_app_api.security.services.UserDetailsImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_NUTRITIONIST', 'ROLE_PATIENT')")
    public ResponseEntity<ResponseSignIn> getCurrentUserInfo(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        ResponseSignIn currentUserInfo = authService.getCurrentUserInfoByUserDetails(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(currentUserInfo);
    }

    @PostMapping("/auth/signup/patient")
    public ResponseEntity<?> signUpPatient(@RequestBody @Valid RequestSignUpPatient signUpDTO) {
        authService.signUpPatient(signUpDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PostMapping("/auth/signin/patient")
    public ResponseEntity<ResponseSignIn> signInPatient(@RequestBody @Valid RequestSignIn requestSignIn) {
        ResponseSignIn response = authService.signInUser(requestSignIn, RoleName.ROLE_PATIENT);

        ResponseSignIn responseSignIn = new ResponseSignIn();
        responseSignIn.setEmail(response.getEmail());
        responseSignIn.setRole(response.getRole());
        responseSignIn.setFirstName(response.getFirstName());
        responseSignIn.setLastName(response.getLastName());
        responseSignIn.setId(response.getId());
        responseSignIn.setToken(response.getToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, response.getJwtCookie().toString())
                .body(responseSignIn);
    }

    @PostMapping("/auth/signin/nutritionist")
    public ResponseEntity<ResponseSignIn> signInNutritionist(@RequestBody @Valid RequestSignIn requestSignIn) {
        ResponseSignIn response = authService.signInUser(requestSignIn, RoleName.ROLE_NUTRITIONIST);

        ResponseSignIn responseSignIn = new ResponseSignIn();
        responseSignIn.setEmail(response.getEmail());
        responseSignIn.setRole(response.getRole());
        responseSignIn.setFirstName(response.getFirstName());
        responseSignIn.setLastName(response.getLastName());
        responseSignIn.setToken(response.getToken());

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, response.getJwtCookie().toString())
                .body(responseSignIn);
    }

    @PostMapping("/auth/signup/nutritionist")
    public ResponseEntity<?> signUpNutritionist(@RequestBody @Valid RequestSignUpNutritionist signUpDTO) {
        authService.signUpNutritionist(signUpDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PreAuthorize("hasAnyRole('ROLE_NUTRITIONIST', 'ROLE_PATIENT')")
    @PostMapping("/signout")
    public ResponseEntity<?> signOutUser() {
        ResponseCookie cleanJwtCookie = authService.getCleanJwtCookie();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cleanJwtCookie.toString())
                .body(null);
    }
}

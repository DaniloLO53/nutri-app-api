package org.nutri.app.nutri_app_api.security.services;

import org.nutri.app.nutri_app_api.exceptions.ResourceAlreadyExistsException;
import org.nutri.app.nutri_app_api.exceptions.UnprocessableEntityException;
import org.nutri.app.nutri_app_api.security.DTOs.RequestSignIn;
import org.nutri.app.nutri_app_api.security.DTOs.RequestSignUpNutritionist;
import org.nutri.app.nutri_app_api.security.DTOs.RequestSignUpPatient;
import org.nutri.app.nutri_app_api.security.DTOs.ResponseSignIn;
import org.nutri.app.nutri_app_api.security.jwt.JwtUtils;
import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;
import org.nutri.app.nutri_app_api.security.models.users.Patient;
import org.nutri.app.nutri_app_api.security.models.users.RoleName;
import org.nutri.app.nutri_app_api.security.models.users.User;
import org.nutri.app.nutri_app_api.security.repositories.AuthRepository;
import org.nutri.app.nutri_app_api.security.repositories.UserInfoProjection;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AuthRepository authRepository;
    private final JwtUtils jwtUtils;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           PasswordEncoder passwordEncoder,
                           AuthRepository authRepository,
                           JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.authRepository = authRepository;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public void signUpPatient(RequestSignUpPatient signUpDTO) {
        String firstName = signUpDTO.getFirstName();
        String lastName = signUpDTO.getLastName();
        String email = signUpDTO.getEmail();
        String password = signUpDTO.getPassword();
        String passwordConfirmation = signUpDTO.getPasswordConfirmation();
        String cpf = signUpDTO.getCpf();

        Boolean patientExistsByEmail = authRepository.existsByEmailAndRole(email, RoleName.ROLE_PATIENT);
        Boolean patientExistsByCpf = authRepository.existsByPatient_CpfAndRole(cpf, RoleName.ROLE_PATIENT);

        if (patientExistsByCpf) throw new ResourceAlreadyExistsException("Paciente", "cpf", cpf);
        if (patientExistsByEmail) throw new ResourceAlreadyExistsException("Paciente", "email", email);
        if (!password.equals(passwordConfirmation)) throw new UnprocessableEntityException("Senhas não coincidem.");

        String encodedPassword = passwordEncoder.encode(password);

        User user = new User(firstName, lastName, email, encodedPassword);
        Patient patient = new Patient(cpf);

        user.setPatient(patient);
        patient.setUser(user);

        user.setRole(RoleName.ROLE_PATIENT);

        authRepository.save(user);
    }

    // TODO: THIS IS A TEMPORARY METHOD - NUTRI WILL HAVE ITS OWN API FOR SIGN UP IN FUTURE
    @Override
    public void signUpNutritionist(RequestSignUpNutritionist signUpDTO) {
        String firstName = signUpDTO.getFirstName();
        String lastName = signUpDTO.getLastName();
        String email = signUpDTO.getEmail();
        String password = signUpDTO.getPassword();
        String crf = signUpDTO.getCrf();

        String encodedPassword = passwordEncoder.encode(password);
        User user = new User(firstName, lastName, email, encodedPassword);

        Nutritionist nutritionist = new Nutritionist(crf);
        user.setNutritionist(nutritionist);
        nutritionist.setUser(user);

        user.setRole(RoleName.ROLE_NUTRITIONIST);

        authRepository.save(user);
    }

    @Override
    public ResponseSignIn signInUser(RequestSignIn requestSignIn, RoleName roleName) {
        String email = requestSignIn.getEmail();
        String password = requestSignIn.getPassword();

        RoleUsernamePasswordAuthToken authToken = new RoleUsernamePasswordAuthToken(email, password, roleName);
        Authentication authentication = authenticationManager.authenticate(authToken);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookieFromUserDetails(userDetails);
        String token = jwtUtils.getJwtFromCookieString(jwtCookie.toString());

        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        String firstName = userDetails.getFirstName();
        String lastName = userDetails.getLastName();
        UUID userId = userDetails.getId();

        ResponseSignIn responseSignIn = new ResponseSignIn();

        responseSignIn.setJwtCookie(jwtCookie);
        responseSignIn.setId(userId.toString());
        responseSignIn.setEmail(email);
        responseSignIn.setFirstName(firstName);
        responseSignIn.setLastName(lastName);
        responseSignIn.setRole(role);
        responseSignIn.setToken(token);

        return responseSignIn;
    }

    @Override
    public ResponseSignIn getCurrentUserInfoByUserDetails(UserDetailsImpl userDetails) {
        ResponseCookie jwtCookie = jwtUtils.generateJwtCookieFromUserDetails(userDetails);
        String token = jwtUtils.getJwtFromCookieString(jwtCookie.toString());

        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        String firstName = userDetails.getFirstName();
        String lastName = userDetails.getLastName();
        String email = userDetails.getUsername();
        UUID userId = userDetails.getId();

        ResponseSignIn responseSignIn = new ResponseSignIn();

        responseSignIn.setJwtCookie(jwtCookie);
        responseSignIn.setId(userId.toString());
        responseSignIn.setEmail(email);
        responseSignIn.setFirstName(firstName);
        responseSignIn.setLastName(lastName);
        responseSignIn.setRole(role);
        responseSignIn.setToken(token);

        return responseSignIn;
    }

    @Override
    public ResponseCookie getCleanJwtCookie() {
        return jwtUtils.getCleanJwtCookie();
    }
}

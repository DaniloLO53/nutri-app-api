package org.nutri.app.nutri_app_api.security.services;

import org.nutri.app.nutri_app_api.security.DTOs.RequestSignIn;
import org.nutri.app.nutri_app_api.security.DTOs.RequestSignUpNutritionist;
import org.nutri.app.nutri_app_api.security.DTOs.RequestSignUpPatient;
import org.nutri.app.nutri_app_api.security.DTOs.ResponseSignIn;
import org.nutri.app.nutri_app_api.security.models.users.RoleName;
import org.nutri.app.nutri_app_api.security.repositories.UserInfoProjection;
import org.springframework.http.ResponseCookie;

public interface AuthService {
    void signUpPatient(RequestSignUpPatient signUpDTO);
    void signUpNutritionist(RequestSignUpNutritionist signUpDTO);
    ResponseSignIn signInUser(RequestSignIn requestSignIn, RoleName roleName);
    UserInfoProjection getCurrentUserInfoByUserDetails(UserDetailsImpl userDetails);
    ResponseCookie getCleanJwtCookie();
}

package org.nutri.app.nutri_app_api.security.repositories;

public interface UserInfoProjection {
    String getEmail();
    String getFirstName();
    String getLastName();
    String getRole();
    String getCpf();
    String getBirthday();
    String getCrf();
}

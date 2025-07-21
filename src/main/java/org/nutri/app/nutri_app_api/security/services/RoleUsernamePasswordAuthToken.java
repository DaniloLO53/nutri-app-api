package org.nutri.app.nutri_app_api.security.services;

import lombok.Getter;
import org.nutri.app.nutri_app_api.security.models.users.RoleName;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class RoleUsernamePasswordAuthToken extends UsernamePasswordAuthenticationToken {
    private final RoleName role;

    // Before authentication
    public RoleUsernamePasswordAuthToken(Object principal, Object credentials, RoleName role) {
        super(principal, credentials);
        this.role = role;
    }

    // After successful authentication
    public RoleUsernamePasswordAuthToken(Object principal, Object credentials, RoleName role, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
        this.role = role;
    }
}

package org.nutri.app.nutri_app_api.security.services;

import org.nutri.app.nutri_app_api.security.models.users.RoleName;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class RoleAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public RoleAuthenticationProvider(UserDetailsServiceImpl userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        RoleUsernamePasswordAuthToken authToken = (RoleUsernamePasswordAuthToken) authentication;
        RoleName role = authToken.getRole();

        UserDetails userDetails = userDetailsService.loadUserByUsernameAndRole(username, role);

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        return new RoleUsernamePasswordAuthToken(userDetails, null, role, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.isAssignableFrom(RoleUsernamePasswordAuthToken.class);
    }
}

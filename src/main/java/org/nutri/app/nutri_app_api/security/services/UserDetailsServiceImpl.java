package org.nutri.app.nutri_app_api.security.services;

import org.nutri.app.nutri_app_api.security.models.users.EntityByRole;
import org.nutri.app.nutri_app_api.security.models.users.RoleName;
import org.nutri.app.nutri_app_api.security.models.users.User;
import org.nutri.app.nutri_app_api.security.repositories.AuthRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthRepository authRepository;

    public UserDetailsServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Since we're using @Transactional, the JPA session doesn't close after this query (so we can use .getRole() at UserDetailsImpl)
        User user = authRepository.findFirstByEmailAndRole(email, RoleName.ROLE_PATIENT.name());

        if (user != null) {
            return UserDetailsImpl.build(user, user.getPatient());
        }

        throw new UsernameNotFoundException("Usuário não encontrado");
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserByUsernameAndRole(String email, RoleName role) throws UsernameNotFoundException {
        // Since we're using @Transactional, the JPA session doesn't close after this query (so we can use .getRole() at UserDetailsImpl)
        User user = authRepository.findFirstByEmailAndRole(email, role.name());

        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        boolean isNutritionist = role.equals(RoleName.ROLE_NUTRITIONIST);
        EntityByRole entityByRole = isNutritionist ? user.getNutritionist() : user.getPatient();

        return UserDetailsImpl.build(user, entityByRole);
    }
}

package org.nutri.app.nutri_app_api.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.nutri.app.nutri_app_api.security.models.users.EntityByRole;
import org.nutri.app.nutri_app_api.security.models.users.RoleName;
import org.nutri.app.nutri_app_api.security.models.users.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserDetailsImpl implements UserDetails {
    // differentiates serializable objects, because UserDetails is serializable (good practice)
    @Serial
    private static final long serialVersionUID = 1L;

    @EqualsAndHashCode.Include
    @Getter
    private UUID id;

    private String email;

    @Getter
    private String firstName;

    @Getter
    private String lastName;

    @JsonIgnore // don't serialize password (sensitive information)
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    @Getter
    private EntityByRole entityByRole;

    public static UserDetailsImpl build(User user, EntityByRole entityByRole) {
        UUID id = user.getId();
        String email = user.getEmail();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String password = user.getPassword();
        RoleName role = user.getRole();

        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.name());
        List<SimpleGrantedAuthority> authorities = List.of(authority);

        return new UserDetailsImpl(id, email, firstName, lastName, password, authorities,entityByRole);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }
}

package org.nutri.app.nutri_app_api.security.repositories;

import org.nutri.app.nutri_app_api.security.models.users.RoleName;
import org.nutri.app.nutri_app_api.security.models.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuthRepository extends JpaRepository<User, UUID> {
    Boolean existsByEmailAndRole(String email, RoleName roleName);
    Boolean existsByPatient_CpfAndRole(String cpf, RoleName roleName);

    @Query(
            nativeQuery = true,
            value = "SELECT u.* FROM users u " +
                    "WHERE u.email = :emailPlaceholder AND u.role = :rolePlaceholder"
    )
    User findFirstByEmailAndRole(@Param("emailPlaceholder") String email, @Param("rolePlaceholder") String role);

    @Query(
            nativeQuery = true,
            value = "SELECT u.id, u.first_name AS firstName, u.last_name AS lastName, u.email, u.role, p.cpf, p.birthday, n.crf " +
                    "FROM users u " +
                    "LEFT JOIN patients p ON p.user_id = u.id " +
                    "LEFT JOIN nutritionists n ON n.user_id = u.id " +
                    "WHERE u.id = :userId;"
    )
    UserInfoProjection findUserInfosById(@Param("userId") UUID userId);
}

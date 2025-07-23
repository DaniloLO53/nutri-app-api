package org.nutri.app.nutri_app_api.repositories.nutritionistRepository;

import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface NutritionistRepository extends JpaRepository<Nutritionist, UUID>, NutritionistRepositoryCustom {
    Optional<Nutritionist> findFirstByUser_Id(UUID userId);

    @Query(
            nativeQuery = true,
            value = "SELECT u.first_name AS firstName, u.last_name AS lastName, u.email, n.crf, n.accepts_remote, " +
                    "l.address, l.phone1, l.phone2, l.phone3, l.ibge_api_state_id AS ibgeApiStateId, " +
                    "l.ibge_api_city AS ibgeApiCity, l.ibge_api_state AS ibgeApiState " +
                    "FROM users u " +
                    "LEFT JOIN nutritionists n ON u.id = n.user_id " +
                    "LEFT JOIN locations l ON l.nutritionist_id = n.id " +
                    "WHERE u.id = :userId;"
    )
    Set<NutritionistProfileFlatProjection> findNutritionistProfile(@Param("userId") UUID userId);

    Optional<Nutritionist> findFirstById(UUID id);
}

package org.nutri.app.nutri_app_api.repositories.locationRepository;

import org.nutri.app.nutri_app_api.models.locations.Location;
import org.nutri.app.nutri_app_api.models.schedules.Schedule;
import org.nutri.app.nutri_app_api.payloads.locationDTOs.OwnLocationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;
import java.util.UUID;

@Repository
public interface LocationRepository extends JpaRepository<Location, UUID> {
    Set<OwnLocationProjection> findByNutritionist_User_Id(UUID nutritionistUserId);
}

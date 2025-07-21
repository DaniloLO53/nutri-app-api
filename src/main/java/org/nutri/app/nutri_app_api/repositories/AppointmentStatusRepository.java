package org.nutri.app.nutri_app_api.repositories;

import org.nutri.app.nutri_app_api.models.appointments.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentStatusRepository extends JpaRepository<AppointmentStatus, UUID> {
    Optional<AppointmentStatus> findFirstByName(String statusName);
}

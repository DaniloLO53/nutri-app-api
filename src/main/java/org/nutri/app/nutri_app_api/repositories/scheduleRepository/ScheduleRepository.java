package org.nutri.app.nutri_app_api.repositories.scheduleRepository;

import org.nutri.app.nutri_app_api.models.schedules.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    Optional<Schedule> findFirstById(UUID id);

    @Query(
            nativeQuery = true,
            value = "SELECT pa.id, pa.start_time, pa.duration_minutes " +
                    "FROM schedules pa " +
                    "WHERE pa.nutritionist_id = :nutritionistId " +
                    "AND pa.start_time >= :startTimePlaceholder " +
                    "AND pa.start_time < :endTimePlaceholder " +
                    "ORDER BY pa.start_time;"
    )
    Set<ScheduleProjection> findSchedulesByStartAndEndDate(
            @Param("nutritionistId") UUID nutritionistId,
            @Param("startTimePlaceholder") LocalDateTime startTime,
            @Param("endTimePlaceholder") LocalDateTime endTime);

     @Query(
        nativeQuery = true,
        value = "SELECT " +
                "    schedule.id AS scheduleId, " +
                "    schedule.start_time AS startTime, " +
                "    schedule.duration_minutes AS durationMinutes, " +
                "    app.id AS appointmentId, " +
                "    CONCAT(u.first_name, ' ', u.last_name) AS patientName, " +
                "    u.id AS patientId, " +
                "    u.email AS patientEmail, " +
                "    s.name AS status, " +
                "    CASE WHEN app.id IS NOT NULL THEN 'APPOINTMENT' ELSE 'SCHEDULE' END AS type " +
                "FROM " +
                "    schedules schedule " +
                "LEFT JOIN " +
                "    appointments app ON schedule.id = app.schedule_id " +
                "LEFT JOIN " +
                "    patients p ON app.patient_id = p.id " +
                "LEFT JOIN " +
                "    users u ON p.user_id = u.id " +
                "LEFT JOIN " +
                "    appointments_status s ON app.appointments_status_id = s.id " +
                "WHERE " +
                "    schedule.nutritionist_id = :nutritionistId " +
                "    AND schedule.start_time >= :startDateTime " +
                "    AND schedule.start_time < :endDateTime " +
                "ORDER BY " +
                "    schedule.start_time ASC"
    )
    Set<OwnScheduleProjection> findOwnSchedulesByStartAndEndDate(
            @Param("nutritionistId") UUID nutritionistId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
}

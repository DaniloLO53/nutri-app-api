package org.nutri.app.nutri_app_api.repositories.scheduleRepository;

import org.nutri.app.nutri_app_api.models.schedules.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, UUID> {
    @Query(
            nativeQuery = true,
            value = "SELECT s.id AS scheduleId, s.start_time AS startTime, s.duration_minutes AS durationMinutes, " +
                    "app.id AS appointmentId, CONCAT(u.first_name, ' ', u.last_name) AS patientName, " +
                    "p.id AS patientId, u.email AS patientEmail, aps.name AS status, l.address, l.id as locationId, " +
                    "CASE WHEN app.id IS NOT NULL THEN 'APPOINTMENT' ELSE 'SCHEDULE' END AS type " +
                    "FROM schedules s " +
                    "LEFT JOIN appointments app ON s.id = app.schedule_id " +
                    "LEFT JOIN patients p ON app.patient_id = p.id " +
                    "LEFT JOIN users u ON p.user_id = u.id " +
                    "LEFT JOIN appointments_status aps ON app.appointments_status_id = aps.id " +
                    "LEFT JOIN locations l ON s.location_id = l.id " +
                    "WHERE l.nutritionist_id = :nutritionistId " +
                    "    AND s.start_time >= :startDateTime " +
                    "    AND s.start_time < :endDateTime " +
                    "ORDER BY s.start_time ASC;"
    )
    Set<OwnScheduleProjection> findOwnSchedulesByStartAndEndDate(
            @Param("nutritionistId") UUID nutritionistId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query(
            nativeQuery = true,
            value = "SELECT s.id AS scheduleId, s.start_time AS startTime, s.duration_minutes AS durationMinutes, " +
                    "app.id AS appointmentId, CONCAT(u.first_name, ' ', u.last_name) AS patientName, " +
                    "p.id AS patientId, u.email AS patientEmail, aps.name AS status, l.address, l.id as locationId, " +
                    "CASE WHEN app.id IS NOT NULL THEN 'APPOINTMENT' ELSE 'SCHEDULE' END AS type " +
                    "FROM schedules s " +
                    "LEFT JOIN appointments app ON s.id = app.schedule_id " +
                    "LEFT JOIN patients p ON app.patient_id = p.id " +
                    "LEFT JOIN users u ON p.user_id = u.id " +
                    "LEFT JOIN appointments_status aps ON app.appointments_status_id = aps.id " +
                    "LEFT JOIN locations l ON s.location_id = l.id " +
                    "WHERE l.nutritionist_id = :nutritionistId " +
                    "  AND s.start_time >= :startDateTime " +
                    "  AND s.start_time < :endDateTime " +
                    "  AND s.location_id = :locationId " +
                    "ORDER BY s.start_time ASC;"
    )
    Set<OwnScheduleProjection> findOwnSchedulesByStartAndEndDateAndLocation(
            @Param("nutritionistId") UUID nutritionistId,
            @Param("locationId") UUID locationId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );



    @Query(
            value = "SELECT EXISTS (" +
                    "  SELECT 1 " +
                    "  FROM schedules s " +
                    "  JOIN locations l ON s.location_id = l.id " +
                    "  WHERE " +
                    "    l.nutritionist_id = :nutritionistId " +
                    "    AND s.start_time < :scheduleEnd " +
                    "    AND :scheduleStart < (s.start_time + s.duration_minutes * INTERVAL '1 minute')" +
                    ")",
            nativeQuery = true
    )
    boolean existsOverlappingSchedule(
            @Param("nutritionistId") UUID nutritionistId,
            @Param("scheduleStart") LocalDateTime scheduleStart,
            @Param("scheduleEnd") LocalDateTime scheduleEnd
    );

    Optional<Schedule> findFirstByAppointment_Id(UUID appointmentId);
}

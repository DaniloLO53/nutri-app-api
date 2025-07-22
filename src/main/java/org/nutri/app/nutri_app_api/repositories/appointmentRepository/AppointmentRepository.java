package org.nutri.app.nutri_app_api.repositories.appointmentRepository;

import org.nutri.app.nutri_app_api.models.appointments.Appointment;
import org.nutri.app.nutri_app_api.security.models.users.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    Boolean existsByPatient(Patient patient);

    @Query(
            nativeQuery = true,
            value = "SELECT EXISTS (" +
                    "SELECT 1 " +
                    "FROM appointments a " +
                    "JOIN appointments_status s ON a.appointments_status_id = s.id " +
                    "JOIN schedules pa ON a.schedule_id = pa.id " +
                    "WHERE a.patient_id = :patientId " +
                    "AND s.name IN (:statusName1, :statusName2) " +
                    "AND pa.start_time > :startTime);"
    )
    boolean patientAlreadyHasSchedule(
            @Param("patientId") UUID patientId,
            @Param("statusName1") String statusName1,
            @Param("statusName2") String statusName2,
            @Param("startTime") LocalDateTime startTime
    );

    @Query(
            nativeQuery = true,
            value = "SELECT EXISTS (" +
                    "SELECT 1 FROM appointments a " +
                    "JOIN schedules pa ON a.schedule_id = pa.id " +
                    "WHERE pa.nutritionist_id = :nutritionistId " +
                    "AND :newAppointmentStart < (pa.start_time + (pa.duration_minutes * INTERVAL '1 minute')) " +
                    "AND :newAppointmentEnd > pa.start_time);"
    )
    boolean hasOverlappingAppointment(
            @Param("nutritionistId") UUID nutritionistId,
            @Param("newAppointmentStart") LocalDateTime newAppointmentStart,
            @Param("newAppointmentEnd") LocalDateTime newAppointmentEnd
    );

    @Query(
            nativeQuery = true,
            value = "SELECT a.id, CONCAT(u_nutritionist.first_name, ' ', u_nutritionist.last_name) AS nutritionistName, " +
                    "u_nutritionist.email AS nutritionistEmail, u_nutritionist.id AS nutritionistId, pa.start_time AS startTime, " +
                    "pa.duration_minutes AS durationMinutes, aps.name AS status, a.is_remote AS isRemote " +
                    "FROM appointments a " +
                    "LEFT JOIN appointments_status aps ON aps.id = a.appointments_status_id " +
                    "LEFT JOIN patients pt ON pt.id = a.patient_id " +
                    "LEFT JOIN schedules pa ON pa.id = a.schedule_id " +
                    "LEFT JOIN nutritionists p ON p.id = pa.nutritionist_id " +
                    "LEFT JOIN users u_nutritionist ON u_nutritionist.id = p.user_id " +
                    "WHERE pt.user_id = :userIdPlaceholder " +
                    "AND aps.name IN ('AGENDADO', 'CONFIRMADO');"
    )
    Set<AppointmentPatientProjection> findPatientFutureAppointments(
            @Param("userIdPlaceholder") UUID userId
    );

    @Query(
            nativeQuery = true,
            value = "SELECT a.id, CONCAT(u_patient.first_name, ' ', u_patient.last_name) AS patientName, u_patient.email AS patientEmail, " +
                    "u_patient.id AS patientId, s.start_time AS startTime, a.is_remote AS isRemote, " +
                    "s.duration_minutes AS durationMinutes, aps.name AS status " +
                    "FROM appointments a " +
                    "LEFT JOIN appointments_status aps ON aps.id = a.appointments_status_id " +
                    "LEFT JOIN patients pt ON pt.id = a.patient_id " +
                    "LEFT JOIN schedules s ON s.id = a.schedule_id " +
                    "LEFT JOIN nutritionists n ON n.id = s.nutritionist_id " +
                    "LEFT JOIN users u_patient ON u_patient.id = pt.user_id " +
                    "WHERE n.user_id = :userId " +
                    "AND aps.name IN ('AGENDADO', 'CONFIRMADO');"
    )
    Set<AppointmentNutritionistProjection> findNutritionistFutureAppointments(
            @Param("userId") UUID userId
    );

    Optional<Appointment> findFirstById(UUID id);
}

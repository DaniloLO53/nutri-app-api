package org.nutri.app.nutri_app_api.repositories.patientRepository;

import org.nutri.app.nutri_app_api.security.models.users.Patient;
import org.nutri.app.nutri_app_api.services.patientService.NutritionistPatientSearchProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findFirstByUser_Id(UUID userId);

    @Query(
            nativeQuery = true,
            value = "SELECT p.id, CONCAT(u.first_name, ' ', u.last_name) AS fullName, u.email " +
                    "FROM users u " +
                    "LEFT JOIN patients p ON p.user_id = u.id " +
                    "WHERE LOWER(unaccent(CONCAT(u.first_name, ' ', u.last_name))) LIKE LOWER(unaccent(CONCAT('%', :name, '%')));"
    )
    Set<PatientSearchByNameProjection> findByName(@Param("name") String name);

    @Query(
            value = "SELECT " +
                    "  p.id as id, " +
                    "  CONCAT(u.first_name, ' ', u.last_name) as name, " +
                    "  ( " +
                    "    SELECT MAX(s.start_time) " +
                    "    FROM appointments a " +
                    "    JOIN schedules s ON a.schedule_id = s.id " +
                    "    WHERE a.patient_id = p.id " +
                    "  ) as lastAppointmentDate " +
                    "FROM " +
                    "  patient_nutritionist_relationships pnr " +
                    "JOIN " +
                    "  patients p ON pnr.patient_id = p.id " +
                    "JOIN " +
                    "  users u ON p.user_id = u.id " +
                    "WHERE " +
                    "  pnr.nutritionist_id = :nutritionistId",
            countQuery = "SELECT COUNT(p.id) " +
                    "FROM patient_nutritionist_relationships pnr " +
                    "JOIN patients p ON pnr.patient_id = p.id " +
                    "WHERE pnr.nutritionist_id = :nutritionistId",
            nativeQuery = true
    )
    Page<NutritionistPatientSearchProjection> findNutritionistPatients(
            @Param("nutritionistId") UUID nutritionistId,
            Pageable pageable
    );

    @Query(
            value = "SELECT DISTINCT " +
                    "  p.id AS id, " +
                    "  CONCAT(u.first_name, ' ', u.last_name) AS name " +
                    "FROM " +
                    "  appointments a " +
                    "JOIN " +
                    "  appointments_status aps ON a.appointments_status_id = aps.id " +
                    "JOIN " +
                    "  schedules s ON a.schedule_id = s.id " +
                    "JOIN " +
                    "  locations l ON s.location_id = l.id " +
                    "JOIN " +
                    "  nutritionists n ON l.nutritionist_id = n.id " +
                    "JOIN " +
                    "  patients p ON a.patient_id = p.id " +
                    "JOIN " +
                    "  users u ON p.user_id = u.id " +
                    "WHERE " +
                    "  n.user_id = :userId " +
                    "  AND aps.name IN ('AGENDADO', 'CONFIRMADO', 'CONCLUIDO', 'ESPERANDO_CONFIRMACAO') " +
                    "  AND (CONCAT(u.first_name, ' ', u.last_name) ILIKE CONCAT('%', :name, '%'))",
            countQuery = "SELECT COUNT(DISTINCT p.id) " +
                    "FROM " +
                    "  appointments a " +
                    "JOIN " +
                    "  appointments_status aps ON a.appointments_status_id = aps.id " +
                    "JOIN " +
                    "  schedules s ON a.schedule_id = s.id " +
                    "JOIN " +
                    "  locations l ON s.location_id = l.id " +
                    "JOIN " +
                    "  nutritionists n ON l.nutritionist_id = n.id " +
                    "JOIN " +
                    "  patients p ON a.patient_id = p.id " +
                    "JOIN " +
                    "  users u ON p.user_id = u.id " +
                    "WHERE " +
                    "  n.user_id = :userId " +
                    "  AND aps.name IN ('AGENDADO', 'CONFIRMADO', 'CONCLUIDO', 'ESPERANDO_CONFIRMACAO') " +
                    "  AND (CONCAT(u.first_name, ' ', u.last_name) ILIKE CONCAT('%', :name, '%'))",
            nativeQuery = true
    )
    Page<NutritionistPatientSearchProjection> findNutritionistScheduledPatientsByName(
            @Param("userId") UUID userId,
            @Param("name") String name,
            Pageable pageable
    );

}

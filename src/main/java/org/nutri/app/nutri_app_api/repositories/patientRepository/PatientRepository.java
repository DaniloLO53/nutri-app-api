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
}

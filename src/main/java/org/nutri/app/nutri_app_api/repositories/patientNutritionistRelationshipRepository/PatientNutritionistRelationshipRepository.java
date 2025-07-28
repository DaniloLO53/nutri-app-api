package org.nutri.app.nutri_app_api.repositories.patientNutritionistRelationshipRepository;

import org.nutri.app.nutri_app_api.models.patientNutritionistRelationship.PatientNutritionistRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PatientNutritionistRelationshipRepository extends JpaRepository<PatientNutritionistRelationship, UUID> {
    boolean existsByNutritionistIdAndPatientId(UUID nutritionistId, UUID patientId);
}

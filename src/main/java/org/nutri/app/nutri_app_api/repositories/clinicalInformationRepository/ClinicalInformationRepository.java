package org.nutri.app.nutri_app_api.repositories.clinicalInformationRepository;

import org.nutri.app.nutri_app_api.models.clinicalInformations.ClinicalInformation;
import org.nutri.app.nutri_app_api.services.clinicalInformationService.MasterDataProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ClinicalInformationRepository extends JpaRepository<ClinicalInformation, UUID> {
    @Query(
            value = "SELECT ci.* FROM clinical_information ci " +
                    "JOIN patient_nutritionist_relationships pnr ON ci.patient_id = pnr.patient_id " +
                    "JOIN nutritionists n ON pnr.nutritionist_id = n.id " +
                    "WHERE ci.patient_id = :patientId AND n.user_id = :nutritionistUserId " +
                    "ORDER BY ci.assessment_date DESC " +
                    "LIMIT 1",
            nativeQuery = true
    )
    ClinicalInformation findMostRecentByPatientAndNutritionistNative(
            @Param("patientId") UUID patientId,
            @Param("nutritionistUserId") UUID nutritionistUserId
    );

    @Query(
            value = "SELECT id, name, NULL as type, 'SYMPTOM' as source FROM symptoms WHERE is_approved = TRUE " +
                    "UNION ALL " +
                    "SELECT id, name, NULL as type, 'DISEASE' as source FROM diseases WHERE is_approved = TRUE " +
                    "UNION ALL " +
                    "SELECT id, name, type, 'ALLERGEN' as source FROM allergens WHERE is_approved = TRUE " +
                    "UNION ALL " +
                    "SELECT id, name, type, 'MEDICATION_SUPPLEMENT' as source FROM medications_supplements WHERE is_approved = TRUE " +
                    "UNION ALL " +
                    "SELECT id, name, NULL as type, 'FOOD' as source FROM foods WHERE is_approved = TRUE " +
                    "ORDER BY source, name",
            nativeQuery = true
    )
    List<MasterDataProjection> findClinicalInformationMasterData();
}

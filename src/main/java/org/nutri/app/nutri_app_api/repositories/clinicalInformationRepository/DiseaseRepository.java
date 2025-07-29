package org.nutri.app.nutri_app_api.repositories.clinicalInformationRepository;

import org.nutri.app.nutri_app_api.models.clinicalInformations.diseases.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DiseaseRepository extends JpaRepository<Disease, UUID> {
}

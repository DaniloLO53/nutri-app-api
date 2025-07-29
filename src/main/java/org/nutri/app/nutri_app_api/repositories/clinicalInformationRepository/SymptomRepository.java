package org.nutri.app.nutri_app_api.repositories.clinicalInformationRepository;

import org.nutri.app.nutri_app_api.models.clinicalInformations.symptoms.Symptom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SymptomRepository extends JpaRepository<Symptom, UUID> {
}

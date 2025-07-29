package org.nutri.app.nutri_app_api.repositories.clinicalInformationRepository;


import org.nutri.app.nutri_app_api.models.clinicalInformations.allergens.Allergen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AllergenRepository extends JpaRepository<Allergen, UUID> {
}

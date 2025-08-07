package org.nutri.app.nutri_app_api.services.nutritionistService;

import org.nutri.app.nutri_app_api.payloads.nutritionistDTOs.NutritionistProfile;
import org.nutri.app.nutri_app_api.payloads.nutritionistDTOs.ProfileSearchParams;

import java.util.Set;
import java.util.UUID;

public interface NutritionistService {
    NutritionistProfile updateNutritionistProfile(UUID userId, NutritionistProfile nutritionistProfile);
    NutritionistProfile getNutritionistProfile(UUID userId);
    Set<ProfileSearchParams> getProfilesByParams(ProfileSearchParams params);
}

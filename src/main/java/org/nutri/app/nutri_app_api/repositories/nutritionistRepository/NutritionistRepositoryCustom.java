package org.nutri.app.nutri_app_api.repositories.nutritionistRepository;

import java.util.Set;

public interface NutritionistRepositoryCustom {
    Set<ProfileByParamsProjection> findNutritionistProfilesByParams(
            String nutritionistName,
            String ibgeApiCity,
            String ibgeApiState,
            Boolean acceptsRemote
    );
}

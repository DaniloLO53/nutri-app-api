package org.nutri.app.nutri_app_api.repositories.nutritionistRepository;

import java.util.List;
import java.util.Set;

public interface ProfileRepositoryCustom {
    Set<ProfileByParamsProjection> findNutritionistProfilesByParams(
            String nutritionistName,
            String ibgeApiCity,
            String ibgeApiState,
            Boolean acceptsRemote
    );
}

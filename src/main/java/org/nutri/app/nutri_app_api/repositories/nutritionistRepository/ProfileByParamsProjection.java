package org.nutri.app.nutri_app_api.repositories.nutritionistRepository;

import java.util.UUID;

public record ProfileByParamsProjection(
    String nutritionistName,
    UUID id,
    String address,
    Boolean acceptsRemote,
    String ibgeApiCity,
    String ibgeApiState
) {}

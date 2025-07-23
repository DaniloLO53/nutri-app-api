package org.nutri.app.nutri_app_api.services.locationService;

import org.nutri.app.nutri_app_api.payloads.locationDTOs.OwnLocationResponse;

import java.util.Set;
import java.util.UUID;

public interface LocationService {
    Set<OwnLocationResponse> getOwnLocations(UUID userId);
}

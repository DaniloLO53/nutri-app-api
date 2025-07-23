package org.nutri.app.nutri_app_api.services.locationService;

import org.nutri.app.nutri_app_api.payloads.locationDTOs.OwnLocationResponse;
import org.nutri.app.nutri_app_api.repositories.locationRepository.LocationRepository;
import org.nutri.app.nutri_app_api.repositories.locationRepository.OwnLocationProjection;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public Set<OwnLocationResponse> getOwnLocations(UUID userId) {
        Set<OwnLocationProjection> locations = locationRepository.findByNutritionist_User_Id(userId);
        Set<OwnLocationResponse> responses = new HashSet<>();

        locations.forEach(location -> {
            OwnLocationResponse ownLocationResponse = new OwnLocationResponse();

            ownLocationResponse.setId(location.getId().toString());
            ownLocationResponse.setAddress(location.getAddress());

            responses.add(ownLocationResponse);
        });

        return responses;
    }
}

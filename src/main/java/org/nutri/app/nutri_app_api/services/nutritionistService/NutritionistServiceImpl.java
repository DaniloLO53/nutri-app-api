package org.nutri.app.nutri_app_api.services.nutritionistService;

import org.modelmapper.ModelMapper;
import org.nutri.app.nutri_app_api.exceptions.ResourceNotFoundException;
import org.nutri.app.nutri_app_api.models.locations.Location;
import org.nutri.app.nutri_app_api.payloads.locationDTOs.LocationDTO;
import org.nutri.app.nutri_app_api.payloads.nutritionistDTOs.NutritionistProfile;
import org.nutri.app.nutri_app_api.payloads.nutritionistDTOs.ProfileSearchParamsDTO;
import org.nutri.app.nutri_app_api.repositories.nutritionistRepository.NutritionistProfileFlatProjection;
import org.nutri.app.nutri_app_api.repositories.nutritionistRepository.ProfileByParamsProjection;
import org.nutri.app.nutri_app_api.repositories.nutritionistRepository.NutritionistRepository;
import org.nutri.app.nutri_app_api.security.models.users.Nutritionist;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NutritionistServiceImpl implements NutritionistService {
    private final NutritionistRepository nutritionistRepository;
    private final ModelMapper modelMapper;

    public NutritionistServiceImpl(
            NutritionistRepository nutritionistRepository,
            ModelMapper modelMapper) {
        this.nutritionistRepository = nutritionistRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public NutritionistProfile getNutritionistProfile(UUID userId) {
        Set<NutritionistProfileFlatProjection> projection = nutritionistRepository.findNutritionistProfile(userId);
        return mapToNested(projection);
    }

    @Override
    public NutritionistProfile updateNutritionistProfile(UUID userId, NutritionistProfile nutritionistProfile) {
        Set<LocationDTO> locationDTOS = nutritionistProfile.getLocations();
        String crf = nutritionistProfile.getCrf();
        String email = nutritionistProfile.getEmail();
        String firstName = nutritionistProfile.getFirstName();
        String lastName = nutritionistProfile.getLastName();
        Boolean acceptsRemote = nutritionistProfile.getAcceptsRemote();

        Nutritionist nutritionist = nutritionistRepository
                .findFirstByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário", "id", userId.toString()));

        nutritionist.setAcceptsRemote(acceptsRemote);
        nutritionist.getUser().setEmail(email);
        nutritionist.getUser().setFirstName(firstName);
        nutritionist.getUser().setLastName(lastName);
        nutritionist.setCrf(crf);

        Set<Location> newLocations = locationDTOS
                .stream()
                .map(locationDTO -> {
                    Location location = modelMapper.map(locationDTO, Location.class);
                    location.setNutritionist(nutritionist);
                    return location;
                })
                .collect(Collectors.toSet());

        Set<Location> existingLocations = nutritionist.getLocations();
        existingLocations.clear();
        existingLocations.addAll(newLocations);

        Nutritionist savedProfile = nutritionistRepository.save(nutritionist);
        NutritionistProfile savedProfileDTO = modelMapper.map(savedProfile, NutritionistProfile.class);

        savedProfileDTO.setEmail(email);
        savedProfileDTO.setFirstName(firstName);
        savedProfileDTO.setFirstName(firstName);

        return savedProfileDTO;
    }

    @Override
    public Set<ProfileSearchParamsDTO> getProfilesByParams(ProfileSearchParamsDTO params) {
        String nutritionistName = params.getNutritionistName();
        String ibgeApiCity = params.getIbgeApiCity();
        String ibgeApiState = params.getIbgeApiState();
        Boolean acceptsRemote = params.getAcceptsRemote() != null ? Boolean.parseBoolean(params.getAcceptsRemote()) : null;

        Set<ProfileByParamsProjection> schedules = nutritionistRepository
                .findNutritionistProfilesByParams(nutritionistName, ibgeApiCity, ibgeApiState, acceptsRemote);

        Set<ProfileSearchParamsDTO> dtos = new HashSet<>();

        schedules.forEach(schedule -> {
            ProfileSearchParamsDTO scheduleSearchParamsDTO = new ProfileSearchParamsDTO();

            scheduleSearchParamsDTO.setNutritionistName(schedule.nutritionistName());
            scheduleSearchParamsDTO.setId(schedule.id().toString());
            scheduleSearchParamsDTO.setAddress(schedule.address());
            scheduleSearchParamsDTO.setIbgeApiCity(schedule.ibgeApiCity());
            scheduleSearchParamsDTO.setIbgeApiState(schedule.ibgeApiState());
            scheduleSearchParamsDTO.setAcceptsRemote(schedule.acceptsRemote().toString());

            dtos.add(scheduleSearchParamsDTO);
        });

        return dtos;
    }

    private NutritionistProfile mapToNested(Set<NutritionistProfileFlatProjection> flatResults) {
        Set<LocationDTO> locationDTOS = getLocationDTOsFromFlatProjections(flatResults);

        NutritionistProfileFlatProjection profile = flatResults
                .stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Perfil não encontrado"));

        return createNutritionistProfileFromFlatProjection(profile, locationDTOS);
    }

    private Set<LocationDTO> getLocationDTOsFromFlatProjections(Set<NutritionistProfileFlatProjection> flatResults) {
        Set<LocationDTO> locationDTOs = new HashSet<>();

        flatResults.forEach(projection -> {
            LocationDTO locationDTO = new LocationDTO();

            locationDTO.setAddress(projection.getAddress());
            locationDTO.setPhone1(projection.getPhone1());
            locationDTO.setPhone2(projection.getPhone2());
            locationDTO.setPhone3(projection.getPhone3());
            locationDTO.setIbgeApiIdentifierState(projection.getIbgeApiStateId());
            locationDTO.setIbgeApiCity(projection.getIbgeApiCity());
            locationDTO.setIbgeApiState(projection.getIbgeApiState());

            locationDTOs.add(locationDTO);
        });

        return locationDTOs;
    }

    private NutritionistProfile createNutritionistProfileFromFlatProjection(NutritionistProfileFlatProjection profile, Set<LocationDTO> locationDTOs) {
        String firstName = profile.getFirstName();
        String lastName = profile.getLastName();
        String email = profile.getEmail();
        String crf = profile.getCrf();
        Boolean acceptsRemote = profile.getAcceptsRemote();

        NutritionistProfile nutritionistProfile = new NutritionistProfile();

        nutritionistProfile.setLocations(locationDTOs);

        nutritionistProfile.setFirstName(firstName);
        nutritionistProfile.setLastName(lastName);
        nutritionistProfile.setEmail(email);
        nutritionistProfile.setCrf(crf);
        nutritionistProfile.setAcceptsRemote(acceptsRemote);
        return nutritionistProfile;
    }
}

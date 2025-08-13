package org.nutri.app.nutri_app_api.services.nutritionistService;

import org.modelmapper.ModelMapper;
import org.nutri.app.nutri_app_api.exceptions.ResourceNotFoundException;
import org.nutri.app.nutri_app_api.models.locations.Location;
import org.nutri.app.nutri_app_api.payloads.locationDTOs.LocationDTO;
import org.nutri.app.nutri_app_api.payloads.nutritionistDTOs.NutritionistProfile;
import org.nutri.app.nutri_app_api.payloads.nutritionistDTOs.ProfileSearchParams;
import org.nutri.app.nutri_app_api.payloads.nutritionistDTOs.ProfileSearchParamsLocation;
import org.nutri.app.nutri_app_api.repositories.nutritionistRepository.NutritionistProfileFlatProjection;
import org.nutri.app.nutri_app_api.repositories.nutritionistRepository.NutritionistRepository;
import org.nutri.app.nutri_app_api.repositories.nutritionistRepository.ProfileByParamsProjection;
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
    public Set<ProfileSearchParams> getProfilesByParams(ProfileSearchParams params) {
        String nutritionistName = params.getNutritionistName();
        String ibgeApiCity = params.getIbgeApiCity();
        String ibgeApiState = params.getIbgeApiState();
        Boolean acceptsRemote = params.getAcceptsRemote() != null ? params.getAcceptsRemote() : null;

        Set<ProfileByParamsProjection> schedules = nutritionistRepository
                .findNutritionistProfilesByParams(nutritionistName, ibgeApiCity, ibgeApiState, acceptsRemote);

        Set<ProfileSearchParams> dtos = new HashSet<>();

        schedules.forEach(schedule -> {
            ProfileSearchParams scheduleSearchParamsDTO = new ProfileSearchParams();
            ProfileSearchParamsLocation location = new ProfileSearchParamsLocation();

            location.setId(schedule.locationId());
            location.setAddress(schedule.locationName());
            scheduleSearchParamsDTO.setLocation(location);

            scheduleSearchParamsDTO.setNutritionistName(schedule.nutritionistName());
            scheduleSearchParamsDTO.setId(schedule.id().toString());
            scheduleSearchParamsDTO.setIbgeApiCity(schedule.ibgeApiCity());
            scheduleSearchParamsDTO.setIbgeApiState(schedule.ibgeApiState());
            scheduleSearchParamsDTO.setAcceptsRemote(schedule.acceptsRemote());

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

    // TODO: refatorar esse código horroroso
    private Set<LocationDTO> getLocationDTOsFromFlatProjections(Set<NutritionistProfileFlatProjection> flatResults) {
    return flatResults.stream()
        .filter(p ->
            p.getAddress() != null ||
            p.getPhone1() != null ||
            p.getPhone2() != null ||
            p.getPhone3() != null ||
            p.getIbgeApiStateId() != null ||
            p.getIbgeApiCity() != null ||
            p.getIbgeApiState() != null
        )
        .map(p -> {
            LocationDTO dto = new LocationDTO();
            dto.setAddress(p.getAddress());
            dto.setPhone1(p.getPhone1());
            dto.setPhone2(p.getPhone2());
            dto.setPhone3(p.getPhone3());
            dto.setIbgeApiIdentifierState(p.getIbgeApiStateId());
            dto.setIbgeApiCity(p.getIbgeApiCity());
            dto.setIbgeApiState(p.getIbgeApiState());
            return dto;
        })
        .collect(Collectors.toSet());
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

package pl.codest.cities.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.codest.cities.model.City;
import pl.codest.cities.repository.CityRepository;

import java.util.Collections;

import static pl.codest.cities.api.CityController.UpdateCityResponse;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    public static final String CITY_NOT_FOUND_MESSAGE = "City not found, id: ";

    public Page<City> find(String name, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        if(StringUtils.isBlank(name)) {
            return cityRepository.findAll(pageable);
        } else {
            return cityRepository.findByNameContainsIgnoreCase(name, pageable);
        }
    }

    @Transactional
    public UpdateCityResponse updateCity(UpdateCityCommand command) {
        return cityRepository
                .findById(command.id())
                .map(city -> {
                    updateFields(command, city);
                    return UpdateCityResponse.SUCCESS;
                })
                .orElseGet(() -> new UpdateCityResponse(
                        false, Collections.singletonList(CITY_NOT_FOUND_MESSAGE + command.id())));
    }

    private void updateFields(UpdateCityCommand command, City city) {
        if (StringUtils.isNotBlank(command.name())) {
            city.setName(command.name());
        }
        if (StringUtils.isNotBlank(command.imageUrl())) {
            city.setImageUrl(command.imageUrl());
        }
    }

    public record UpdateCityCommand(int id, String name, String imageUrl){}
}

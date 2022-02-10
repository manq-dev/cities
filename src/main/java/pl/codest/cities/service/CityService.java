package pl.codest.cities.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.codest.cities.model.City;
import pl.codest.cities.repository.CityRepository;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public Page<City> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return cityRepository.findAll(pageable);
    }


}

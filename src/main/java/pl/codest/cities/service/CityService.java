package pl.codest.cities.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.codest.cities.repository.CityRepository;

@Service
@RequiredArgsConstructor
class CityService {

    private final CityRepository cityRepository;


}

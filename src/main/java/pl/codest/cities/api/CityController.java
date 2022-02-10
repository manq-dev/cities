package pl.codest.cities.api;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.codest.cities.model.City;
import pl.codest.cities.service.CityService;

import static pl.codest.cities.api.CityController.CITIES_MAPPING;

@RequiredArgsConstructor
@RestController
@RequestMapping( CITIES_MAPPING)
class CityController {

    private final CityService cityService;
    static final String CITIES_MAPPING = "/cities";

    @GetMapping
    public Page<City> findAllCities(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false ,defaultValue = "10") Integer size) {
        return cityService.findAll(page, size);
    }

}

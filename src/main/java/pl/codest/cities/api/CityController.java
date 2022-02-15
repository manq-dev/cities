package pl.codest.cities.api;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pl.codest.cities.api.UpdateCityValidator.UpdateCityValidation;
import pl.codest.cities.model.City;
import pl.codest.cities.service.CityService;
import pl.codest.cities.service.CityService.UpdateCityCommand;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static java.util.Collections.emptyList;
import static pl.codest.cities.api.CityController.CITIES_MAPPING;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping( CITIES_MAPPING)
@CrossOrigin(origins = "http://localhost:4200")
public class CityController {

    static final String CITIES_MAPPING = "/cities";
    private final CityService cityService;

    private static final String DEFAULT_LIST_SIZE = "10";
    private static final String DEFAULT_PAGE_NUMBER = "0";

    static final String SIZE_VALIDATION_ERROR = "Size must be greater than 0";
    static final String PAGE_VALIDATION_ERROR = "Page must be greater or equal 0";
    static final String ID_VALIDATION_ERROR = "Id must be greate or equal 0";
    static final String REQUEST_VALIDATION_ERROR = "One of two fields in request should be specified.";

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<City> findAllCities(
            @RequestParam(required = false) String name,
            @RequestParam(required = false, defaultValue = DEFAULT_PAGE_NUMBER) @PositiveOrZero(message = PAGE_VALIDATION_ERROR) Integer page,
            @RequestParam(required = false ,defaultValue = DEFAULT_LIST_SIZE) @Positive(message = SIZE_VALIDATION_ERROR) Integer size) {
        if(StringUtils.isNotBlank(name)) {
            return cityService.findByName(name, page, size);
        }
        return cityService.findAll(page, size);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateCity(
            @PathVariable @PositiveOrZero(message = ID_VALIDATION_ERROR) Integer id,
            @RequestBody @Valid UpdateCityRequest command) {
        UpdateCityResponse response = cityService.updateCity(command.toUpdateCommand(id));
        if (!response.success()) {
            String message = String.join(", ", response.errors());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }

    @UpdateCityValidation(message = REQUEST_VALIDATION_ERROR)
    record UpdateCityRequest(String name, String imageUrl) {
        UpdateCityCommand toUpdateCommand(int id) {
            return new UpdateCityCommand(id, name, imageUrl);
        }
    }

    public record UpdateCityResponse(boolean success, List<String> errors) {
        public static UpdateCityResponse SUCCESS = new UpdateCityResponse(true, emptyList());
    }
}

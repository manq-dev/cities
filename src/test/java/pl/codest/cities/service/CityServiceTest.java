package pl.codest.cities.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import pl.codest.cities.IntegrationTestContext;
import pl.codest.cities.model.City;
import pl.codest.cities.service.CityService.UpdateCityCommand;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static pl.codest.cities.api.CityController.UpdateCityResponse;
import static pl.codest.cities.service.CityService.CITY_NOT_FOUND_MESSAGE;

class CityServiceTest extends IntegrationTestContext {

    @Test
    void shouldFindByName() {
        // given:
        String tokyo = "Tokyo";

        // when:
        Page<City> cityPage = cityService.findByName("oky", 0,5);
        Page<City> cityPage1 = cityService.findByName("Tokyo", 0,5);
        Page<City> cityPage2 = cityService.findByName("kyo", 0,5);

        // then:
        assertEquals(1, cityPage.toList().size());
        assertEquals(tokyo, cityPage.toList().stream().findFirst().get().getName());
        assertEquals(tokyo, cityPage1.toList().stream().findFirst().get().getName());
        assertEquals(tokyo, cityPage2.toList().stream().findFirst().get().getName());
    }

    @Test
    void shouldUpdateCity() {
        // given:
        UpdateCityCommand updateCommand = new UpdateCityCommand(7, "Sao Paolo", "brazil.br");

        // when:
        UpdateCityResponse response = cityService.updateCity(updateCommand);

        // then:
        assertEquals(UpdateCityResponse.SUCCESS, response);
    }

    @Test
    void shopuldNotUpdateCity() {
        // given:
        int id = 7777;
        UpdateCityCommand updateCommand = new UpdateCityCommand(id, "Sao Paolo", "brazil.br");

        // when:
        UpdateCityResponse response = cityService.updateCity(updateCommand);

        // then:
        assertEquals(
                new UpdateCityResponse(false, Collections.singletonList(CITY_NOT_FOUND_MESSAGE + id)),
                response);
    }
}
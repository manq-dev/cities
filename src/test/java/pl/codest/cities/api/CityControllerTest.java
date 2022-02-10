package pl.codest.cities.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MvcResult;
import pl.codest.cities.IntegrationTestContext;
import pl.codest.cities.model.City;
import pl.codest.cities.repository.CityRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.codest.cities.api.CityController.CITIES_MAPPING;

class CityControllerTest extends IntegrationTestContext {

    @MockBean
    protected CityRepository cityRepository;

    private static final List<City> EXPECTED_CITIES = List.of(
            City.builder().id(1).name("Tokyo").build(),
            City.builder().id(2).name("Berlin").build(),
            City.builder().id(3).name("Warsaw").build(),
            City.builder().id(4).name("Madrid").build(),
            City.builder().id(5).name("Lisbon").build());

    @Test
    public void shouldReturnCitiesWithoutRequestParams() throws Exception {
        //given:
        int page = 0;
        int size = 10;
        Page<City> expectedCityPage
                = new PageImpl<>(EXPECTED_CITIES, PageRequest.of(page, size), EXPECTED_CITIES.size());
        when(cityRepository.findAll(PageRequest.of(page, size))).thenReturn(
                expectedCityPage);

        //when:
        MvcResult mvcResult = mockMvc
                .perform(get(CITIES_MAPPING))
                .andExpect(status().isOk())
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new PageDeserializer.PageModule());
        Page<City> parsedResponse = objectMapper.readValue(mvcResult
                .getResponse().getContentAsByteArray(), new TypeReference<>() {});

        //then:
        assertEquals(expectedCityPage, parsedResponse);
        assertEquals(APPLICATION_JSON_VALUE,
                mvcResult.getResponse().getContentType());
    }

    @Test
    public void shouldReturnLimitedCities() throws Exception {
        //given:
        int page = 2;
        int size = 2;
        PageRequest pageable = PageRequest.of(page, size);
        Page<City> expectedCityPage
                = new PageImpl<>(EXPECTED_CITIES, pageable, EXPECTED_CITIES.size());
        when(cityRepository.findAll(pageable)).thenReturn(expectedCityPage);

        //when:
        MvcResult mvcResult = mockMvc
                .perform(get(CITIES_MAPPING)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new PageDeserializer.PageModule());
        Page<City> parsedResponse = objectMapper.readValue(mvcResult
                .getResponse().getContentAsByteArray(), new TypeReference<>() {});

        //then:

        assertEquals(expectedCityPage, parsedResponse);
        assertEquals(APPLICATION_JSON_VALUE,
                mvcResult.getResponse().getContentType());
    }
}
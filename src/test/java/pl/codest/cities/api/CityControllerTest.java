package pl.codest.cities.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.data.domain.Page;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.codest.cities.IntegrationTestContext;
import pl.codest.cities.model.City;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.codest.cities.api.CityController.*;
import static pl.codest.cities.service.CityService.CITY_NOT_FOUND_MESSAGE;

@TestInstance(Lifecycle.PER_CLASS)
class CityControllerTest extends IntegrationTestContext {

    private static final List<City> EXPECTED_CITIES = List.of(
            City.builder().id(1).name("Tokyo").build(),
            City.builder().id(2).name("Jakarta").build(),
            City.builder().id(3).name("Delhi").build(),
            City.builder().id(4).name("Mumbai").build(),
            City.builder().id(5).name("Manila").build());

    @BeforeAll
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    //GET Tests

    @Test
    public void shouldReturnCitiesWithoutRequestParams() throws Exception {
        //given:
        int page = 0;
        int size = 5;

        //when:
        MvcResult mvcResult = mockMvc
                .perform(get(CITIES_MAPPING)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andReturn();

        //then:
        Page<City> parsedResponse = parseToCityPage(mvcResult);

        assertEquals(APPLICATION_JSON_VALUE,
                mvcResult.getResponse().getContentType());
        assertEquals(EXPECTED_CITIES,
                prepareAssertableResultList(parsedResponse));
    }

    @Test
    public void shouldReturnLimitedCities() throws Exception {
        //given:
        int page = 1;
        int size = 2;

        //when:
        MvcResult mvcResult = mockMvc
                .perform(get(CITIES_MAPPING)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andReturn();

        //then:
        Page<City> parsedResponse = parseToCityPage(mvcResult);

        assertEquals(APPLICATION_JSON_VALUE,
                mvcResult.getResponse().getContentType());
        assertEquals(List.of(EXPECTED_CITIES.get(2), EXPECTED_CITIES.get(3)),
                prepareAssertableResultList(parsedResponse));
    }

    @Test
    public void shouldReturnBadRequestWhenPageIncorrect() throws Exception {
        //given:
        int page = -1;

        //when:
        MvcResult mvcResult = mockMvc
                .perform(get(CITIES_MAPPING)
                        .param("page", String.valueOf(page)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then:
        assertTrue(mvcResult.getResponse().getContentAsString().contains(PAGE_VALIDATION_ERROR));
    }

    @Test
    public void shouldReturnBadRequestWhenSizeIncorrect() throws Exception {
        //given:
        int size = 0;

        //when:
        MvcResult mvcResult = mockMvc
                .perform(get(CITIES_MAPPING)
                        .param("size", String.valueOf(size)))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then:
        assertTrue(mvcResult.getResponse().getContentAsString().contains(SIZE_VALIDATION_ERROR));
    }

    @Test
    public void shouldReturnCitiesSearchByName() throws Exception {
        //given:
        int page = 0;
        int size = 10;
        String searchPhrase = "oky";

        //when:
        MvcResult mvcResult = mockMvc
                .perform(get(CITIES_MAPPING)
                        .param("name", searchPhrase))
                .andExpect(status().isOk())
                .andReturn();

        //then:
        Page<City> parsedResponse = parseToCityPage(mvcResult);

        assertEquals(APPLICATION_JSON_VALUE,
                mvcResult.getResponse().getContentType());
        assertEquals(EXPECTED_CITIES.stream().filter(city -> city.getName().contains(searchPhrase)).toList(),
                prepareAssertableResultList(parsedResponse));
    }

    //PATCH Tests

    @Test
    @WithMockUser(roles = "ALLOW_EDIT")
    public void shouldUpdateCityWithSpecifiedId() throws Exception {
        //given:
        int id = 5;
        City expectedCity = City.builder().id(id).name("SHANGHAI").imageUrl("http://shanghai.cn").build();
        String requestJson = prepareRequestForUpdate(expectedCity);

        //when:
        mockMvc
                .perform(patch(String.join("",CITIES_MAPPING, "/", String.valueOf(id)))
                        .content(requestJson)
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isAccepted());

        //then:
        Optional<City> city = cityRepository.findById(id);

        assertEquals(expectedCity, city.get());
    }

    @Test
    @WithMockUser(roles = "ALLOW_EDIT")
    public void shouldThrowBadRequestWhenNoObjectWithId() throws Exception {
        //given:
        int id = 5555;
        City expectedCity = City.builder().id(id).name("SHANGHAI").imageUrl("http://shanghai.cn").build();
        String requestJson = prepareRequestForUpdate(expectedCity);

        //when:
        MvcResult result = mockMvc
                .perform(patch(String.join("",CITIES_MAPPING, "/", String.valueOf(id)))
                        .content(requestJson)
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then:
        assertEquals(CITY_NOT_FOUND_MESSAGE + id,
                result.getResponse().getErrorMessage());
    }

    @Test
    @WithMockUser(roles = "ALLOW_EDIT")
    public void shouldReturnBadRequestWhenIdIncorrectDuringUpdate() throws Exception {
        //given:
        String id = "-5";
        City expectedCity = City.builder().id(Integer.parseInt(id)).name("SHANGHAI").imageUrl("http://shanghai.cn").build();
        String requestJson = prepareRequestForUpdate(expectedCity);

        //when:
        MvcResult mvcResult = mockMvc
                .perform(patch(String.join("/", CITIES_MAPPING, id))
                        .content(requestJson)
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then:
        assertTrue(mvcResult.getResponse().getContentAsString().contains(ID_VALIDATION_ERROR));
    }

    @Test
    @WithMockUser(roles = "ALLOW_EDIT")
    public void shouldReturnBadRequestWhenRequestBodyIncorrectDuringUpdate() throws Exception {
        //given:
        String id = "5";
        City expectedCity = City.builder().id(Integer.parseInt(id)).name("SHANGHAI").imageUrl(null).build();
        String requestJson = prepareRequestForUpdate(expectedCity);

        //when:
        MvcResult mvcResult = mockMvc
                .perform(patch(String.join("/", CITIES_MAPPING, id))
                        .content(requestJson)
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then:
        assertTrue(mvcResult.getResponse().getContentAsString().contains(REQUEST_VALIDATION_ERROR));
    }

    @Test
    @WithMockUser(roles = "ALLOW_EDIT")
    public void shouldThrowBadRequestWhenBadRequest() throws Exception {
        //given:
        int id = 5;
        City requestCity = City.builder().id(id).name(null).imageUrl(null).build();
        String requestJson = prepareRequestForUpdate(requestCity);

        //when:
        MvcResult result = mockMvc
                .perform(patch(String.join("",CITIES_MAPPING, "/", String.valueOf(id)))
                        .content(requestJson)
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andReturn();

        //then:
        assertTrue(result.getResponse().getContentAsString().contains(REQUEST_VALIDATION_ERROR));
    }

    @Test
    public void shouldUnauthorizeWithoutRole() throws Exception {
        // given:
        int id = 5;
        City expectedCity = City.builder().id(id).name("SHANGHAI").imageUrl("http://shanghai.cn").build();
        String requestJson = prepareRequestForUpdate(expectedCity);

        // when:
        mockMvc
                .perform(patch(String.join("",CITIES_MAPPING, "/", String.valueOf(id)))
                        .content(requestJson)
                        .contentType(APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    private Page<City> parseToCityPage(MvcResult mvcResult) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new PageDeserializer.PageModule());
        return objectMapper.readValue(mvcResult
                .getResponse().getContentAsByteArray(), new TypeReference<>() {});
    }

    private List<City> prepareAssertableResultList(Page<City> parsedResponse) {
        return parsedResponse.toList().stream()
                             .map(
                                     city -> City.builder().id(city.getId()).name(city.getName()).build())
                             .toList();
    }

    private String prepareRequestForUpdate(City expectedCity) throws JsonProcessingException {
        UpdateCityRequest requestBody = new UpdateCityRequest(expectedCity.getName(), expectedCity.getImageUrl());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(requestBody);
    }
}
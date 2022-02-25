package pl.codest.cities.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import pl.codest.cities.IntegrationTestContext;
import pl.codest.cities.model.City;

import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
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

    private static final List<CityWrapper> EXPECTED_CITIES = List.of(
            CityWrapper.builder().id(1).name("Tokyo").build(),
            CityWrapper.builder().id(2).name("Jakarta").build(),
            CityWrapper.builder().id(3).name("Delhi").build(),
            CityWrapper.builder().id(4).name("Mumbai").build(),
            CityWrapper.builder().id(5).name("Manila").build());
    private static final String TOTAL_CITIES = "1000";

    @BeforeAll
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    //GET Tests

    @Test
    public void shouldReturnCities() throws Exception {
        //given:
        int page = 0;
        int size = 5;

        //when:
        MvcResult mvcResult = mockMvc
                .perform(get(CITIES_MAPPING)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$['pageable']['paged']").value("true"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$['pageable']['pageSize']").value(String.valueOf(size)))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$['pageable']['pageNumber']").value(String.valueOf(page)))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$['totalElements']").value(TOTAL_CITIES))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.content[*]").isArray())
                .andReturn();

        List<CityWrapper> actualCityList = parseCitiesFrom(mvcResult);

        //then:
        assertEquals(APPLICATION_JSON_VALUE,
                mvcResult.getResponse().getContentType());
        assertEquals(EXPECTED_CITIES,
                actualCityList);
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
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$['pageable']['paged']").value("true"))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$['pageable']['pageSize']").value(String.valueOf(size)))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$['pageable']['pageNumber']").value(String.valueOf(page)))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$['totalElements']").value(TOTAL_CITIES))
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.content[*]").isArray())
                .andReturn();

        //then:
        List<CityWrapper> actualCityList = parseCitiesFrom(mvcResult);

        assertEquals(APPLICATION_JSON_VALUE,
                mvcResult.getResponse().getContentType());
        assertEquals(List.of(EXPECTED_CITIES.get(2), EXPECTED_CITIES.get(3)),
                actualCityList);
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
        String searchPhrase = "oky";

        //when:
        MvcResult mvcResult = mockMvc
                .perform(get(CITIES_MAPPING)
                        .param("name", searchPhrase))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers
                        .jsonPath("$.content[*]").isArray())
                .andReturn();

        //then:
        List<CityWrapper> actualCityList = parseCitiesFrom(mvcResult);

        assertEquals(APPLICATION_JSON_VALUE,
                mvcResult.getResponse().getContentType());
        assertEquals(EXPECTED_CITIES.stream().filter(city -> city.name.contains(searchPhrase)).toList(),
                actualCityList);
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

        assertEquals(expectedCity.getId(), city.get().getId());
        assertEquals(expectedCity.getName(), city.get().getName());
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

    private List<CityWrapper> parseCitiesFrom(MvcResult mvcResult) throws UnsupportedEncodingException {
        String jsonPathToContent = "$['content'][*]";
        DocumentContext jsonContext = JsonPath.parse(mvcResult
                .getResponse().getContentAsString());
        List<LinkedHashMap> pagedContent = jsonContext.read(jsonPathToContent);
        return prepareAssertableResultList(pagedContent);
    }

    private List<CityWrapper> prepareAssertableResultList(List<LinkedHashMap> parsedResponse) {
        return parsedResponse.stream()
                             .map(city -> CityWrapper.builder()
                                                     .id((int)city.get("id"))
                                                     .name((String)city.get("name"))
                                                     .build()).toList();
    }

    private String prepareRequestForUpdate(City expectedCity) throws JsonProcessingException {
        UpdateCityRequest requestBody = new UpdateCityRequest(expectedCity.getName(), expectedCity.getImageUrl());
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        return ow.writeValueAsString(requestBody);
    }
}
package pl.codest.cities.service;

import org.junit.jupiter.api.Test;
import pl.codest.cities.model.City;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CSVServiceTest {

    private final String TEST_CSV_PATH = "src/test/resources/cities-test.csv";

    @Test
    void shouldParseTestFile() {
        // given:

        // when:
        List<City> cities = CSVService.csvToCities(TEST_CSV_PATH);

        // then:
        assertEquals(3, cities.size());
        assertEquals(3, cities.get(2).getId());
        assertEquals("Delhi", cities.get(2).getName());
        assertEquals(
                "https://upload.wikimedia.org/wikipedia/commons/thumb/5/55/IN-DL.svg/439px-IN-DL.svg.png",
                cities.get(2).getImageUrl());
    }
}
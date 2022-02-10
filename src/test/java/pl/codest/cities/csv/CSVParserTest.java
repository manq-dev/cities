package pl.codest.cities.csv;

import org.junit.jupiter.api.Test;
import pl.codest.cities.model.City;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CSVParserTest {

    private final String TEST_CSV_PATH = "db/cities.csv";

    @Test
    void shouldParseTestFile() {
        // given:

        // when:
        List<City> cities = CSVParser.csvToCities(TEST_CSV_PATH);

        // then:
        assertEquals(1000, cities.size());
        assertEquals(3, cities.get(2).getId());
        assertEquals("Delhi", cities.get(2).getName());
        assertEquals("https://upload.wikimedia.org/wikipedia/commons/thumb/5/55/IN-DL.svg/439px-IN-DL.svg.png",
                cities.get(2).getImageUrl());
    }
}
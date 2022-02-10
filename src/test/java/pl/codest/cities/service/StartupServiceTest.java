package pl.codest.cities.service;

import org.junit.jupiter.api.Test;
import pl.codest.cities.IntegrationTestContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StartupServiceTest extends IntegrationTestContext {

    @Test
    void databaseShouldNotBeEmpty() {
        assertEquals(1000, cityRepository.findAll().size());
    }
}
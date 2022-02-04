package pl.codest.cities.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.codest.cities.model.City;
import pl.codest.cities.repository.CityRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class StartupService {

    private final CityRepository cityRepository;
    //TODO Integration test with in-memory H2
    @EventListener(ApplicationReadyEvent.class)
    public void populateDbIfNotEmpty() {
        long citiesCount = cityRepository.count();
        log.info("Number of cities in db: " + citiesCount);
        if (citiesCount == 0 ) {
            List<City> cities = CSVService.csvToCities("cities.csv");
            cityRepository.saveAllAndFlush(cities);
        }

    }
}

package pl.codest.cities.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import pl.codest.cities.csv.CSVParser;
import pl.codest.cities.model.City;
import pl.codest.cities.repository.CityRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
class StartupService {

    private final CityRepository cityRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void populateDbIfNotEmpty() {
        long citiesCount = cityRepository.count();
        if (citiesCount == 0) {
            List<City> cities = CSVParser.csvToCities("db/cities.csv");
            cityRepository.saveAll(cities);
            log.info("Saved " + cities.size() + " cities to DB.");
        } else {
            log.info("DB is set up with " + citiesCount + " cities.");
        }

    }
}

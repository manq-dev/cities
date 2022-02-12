package pl.codest.cities.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${app.csv_file.location}")
    private String CSV_LOCATION;

    @EventListener(ApplicationReadyEvent.class)
    public void populateDbIfNotEmpty() {
        long citiesCount = cityRepository.count();
        if (citiesCount == 0) {
            List<City> cities = CSVParser.csvToCities(CSV_LOCATION);
            cityRepository.saveAll(cities);
            log.info(String.join(" ","Saved", String.valueOf(cities.size()), "cities to DB."));
        } else {
            log.info(String.join(" ", "DB is set up with", String.valueOf(citiesCount), "cities."));
        }

    }
}

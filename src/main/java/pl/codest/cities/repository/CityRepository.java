package pl.codest.cities.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.codest.cities.model.City;

public interface CityRepository extends JpaRepository<City, Long> {

}
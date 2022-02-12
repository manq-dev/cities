package pl.codest.cities.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.codest.cities.model.City;

public interface CityRepository extends JpaRepository<City, Integer> {

    Page<City> findByNameContainsIgnoreCase(String name, Pageable pageable);
}
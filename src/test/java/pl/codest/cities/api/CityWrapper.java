package pl.codest.cities.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import pl.codest.cities.model.City;

//Class used for comparing cities. We don't want to work on entities.
@Builder
@AllArgsConstructor
@EqualsAndHashCode
class CityWrapper {

    int id;
    String name;

    public CityWrapper(City city) {
        id = city.getId();
        name = city.getName();
    }
}

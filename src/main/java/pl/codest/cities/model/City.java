package pl.codest.cities.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class City {

    @Id
    private int id;
    private String name;

    @Column(length = 800)
    private String imageUrl;
}

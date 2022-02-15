import {Component, OnInit} from '@angular/core';
import {CityService} from 'src/app/service/city.service';
import {ActivatedRoute, Router} from '@angular/router';
import {City} from 'src/app/model/city';

@Component({
  selector: 'app-city-form',
  templateUrl: './city-form.component.html',
  styleUrls: ['./city-form.component.css']
})
export class CityFormComponent implements OnInit {

  currentCity: City = {
    id: '',
    name: '',
    imageUrl: ''
  };
  message = '';

  constructor(
    private cityService: CityService,
    private route: ActivatedRoute,
    private router: Router) { }

  ngOnInit(): void {
    this.message = '';
    // this.prepareCity(this.route.snapshot.params.id, this.route.snapshot.params.name, this.route.snapshot.params.imageUrl);
  }

  prepareCity(id: string, name: string, imageUrl: string): void {
    this.currentCity.id = id;
    this.currentCity.name = name;
    this.currentCity.imageUrl = imageUrl;
  }

  updateCity(): void {
    this.message = '';

    this.cityService.update(this.currentCity.id, this.currentCity)
      .subscribe(
        response => {
          console.log(response);
          this.message = response.message ? response.message : 'This city was updated successfully!';
        },
        error => {
          console.log(error);
        });
  }

}
